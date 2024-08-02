package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.annotations.CfgConstructor;
import it.hurts.sskirillss.octolib.config.cfgbuilder.*;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.ScalarEntry;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.*;

import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry.COMPOUND_CFG_TAG;
import static it.hurts.sskirillss.octolib.config.cfgbuilder.DeconstructedObjectEntry.DECONSTRUCTED_CFG_TAG;

public class ConstructorExt extends Constructor {
    
    protected Map<Class<?>, Construct> typeConstructorsMap = new IdentityHashMap<>();
    protected Set<Class<?>> passedClasses = new HashSet<>();
    
    public ConstructorExt(LoaderOptions loadingConfig) {
        this(Object.class, loadingConfig);
    }
    
    public ConstructorExt(Class<?> theRoot, LoaderOptions loadingConfig) {
        super(theRoot, loadingConfig);
        initConstructors();
    }
    
    public ConstructorExt(TypeDescription theRoot, LoaderOptions loadingConfig) {
        super(theRoot, loadingConfig);
        initConstructors();
    }
    
    public ConstructorExt(TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
        super(theRoot, moreTDs, loadingConfig);
        initConstructors();
    }
    
    public ConstructorExt(String theRoot, LoaderOptions loadingConfig) throws ClassNotFoundException {
        super(theRoot, loadingConfig);
        initConstructors();
    }
    
    @Override
    public Object constructObject(Node node) {
        return super.constructObject(node);
    }
    
    protected void initConstructors() {
        yamlClassConstructors.put(NodeId.mapping, new ConstructMappingCustomized());
        yamlClassConstructors.put(NodeId.sequence, new ConstructSequenceCustomizable());
        
        typeConstructorsMap.put(CompoundEntry.class, new ConstructEntry());
        typeConstructorsMap.put(DeconstructedObjectEntry.class, new ConstructMappedEntry());
        typeConstructorsMap.put(ArrayEntry.class, new ConstructMappedEntry());
        
        yamlConstructors.put(COMPOUND_CFG_TAG.yamlTag(), new ConstructEntry());
        yamlConstructors.put(DECONSTRUCTED_CFG_TAG.yamlTag(), new ConstructMappedEntry());
    }
    
    @Override
    protected Object newInstance(Class<?> ancestor, Node node, boolean tryDefault) {
        try {
            Class<?> type = node.getType();
            if (typeDefinitions.containsKey(type)) {
                TypeDescription td = typeDefinitions.get(type);
                final Object instance = td.newInstance(node);
                if (instance != null) {
                    return instance;
                }
            }

            if (tryDefault) {
                /*
                 * Removed <code> have InstantiationException in case of abstract type
                 */
                try {
                    if (type.isAnonymousClass()) {
                        node.setType(type.getSuperclass());
                        return newInstance(ancestor, node, tryDefault);
                    }
                    
                    if (ancestor.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
                        java.lang.reflect.Constructor<?> c = type.getDeclaredConstructor();
                        c.setAccessible(true);
                        return c.newInstance();
                    } else if (node.getTag().isCustomGlobal()) {
                        var newType = getClassForNode(node);
                        if (newType != type && type.isAssignableFrom(newType)) {
                            node.setType(newType);
                            return newInstance(ancestor, node, tryDefault);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    if (node.getNodeId() != NodeId.mapping)
                        throw e;

                    MappingNode mappingNode = (MappingNode) node;

                    java.lang.reflect.Constructor<?> c = type.getDeclaredConstructor(mappingNode.getValue()
                            .stream().map(t -> t.getValueNode().getType()).toArray(Class[]::new));
                    c.setAccessible(true);
                    return c.newInstance(mappingNode.getValue()
                            .stream()
                            .map(NodeTuple::getValueNode)
                            .map(this::constructObject)
                            .toArray(Object[]::new));
                }
            }
        } catch (Exception e) {
            throw new YAMLException(e);
        }

        return NOT_INSTANTIATED_OBJECT;
    }
    
    protected void createDefinition(Class<?> type) {
        var constructors = Arrays.stream(type.getConstructors())
                .filter(c -> c.isAnnotationPresent(CfgConstructor.class))
                .toList();
        
        if (constructors.size() > 1)
            throw new RuntimeException("Only one constructor in class can be annotated with @CfgConstructor.");
        else if (!constructors.isEmpty()) {
            final var c = constructors.get(0);
            final var props = c.getAnnotation(CfgConstructor.class).value();
            c.setAccessible(true);
            
            if (props.length != c.getParameterCount())
                throw new IncompleteAnnotationException(CfgConstructor.class,
                        "Property count must be equal to constructor arguments count.");
            
            var iterator = Arrays.stream(c.getParameterTypes()).iterator();
            for (var prop : props)
                if (getPropertyUtils().getProperty(type, prop).getType().isAssignableFrom(iterator.next()))
                    throw new IncompleteAnnotationException(CfgConstructor.class,
                            "Property types and order must be the same as in constructor.");
            
            this.addTypeDescription(new TypeDescription(type) {
                
                @Override
                public Object newInstance(Node node) {
                    if (node.getNodeId() != NodeId.mapping)
                        return super.newInstance(node);
                    
                    
                    var map = ((MappingNode) node).getValue()
                            .stream()
                            .collect(Collectors.toMap(
                                    t -> ((ScalarNode) t.getKeyNode()).getValue(),
                                    t -> constructObject(t.getValueNode())));
                    try {
                        return c.newInstance(Arrays.stream(props).map(map::get).toArray());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
    
    @Override
    protected Construct getConstructor(Node node) {
        var type = node.getType();
        if (type != null && !passedClasses.contains(type))
            createDefinition(type);
        return super.getConstructor(node);
    }
    
    public class ConstructMappedEntry extends ConstructEntry {
        
        protected ConfigEntry constructMappingObject(Node valueNode) {
            MappingNode mappingNode = (MappingNode) valueNode;
            if (valueNode.getTag() == null || valueNode.getTag() == Tag.MAP) {
                return constructMapping(new CompoundEntry(), (MappingNode) valueNode);
            } else {
                var tag = CfgTag.by(mappingNode.getTag());
                mappingNode.setTag(Tag.MAP);
                mappingNode.setType(DeconstructedObjectEntry.class);
                return constructMapping(new DeconstructedObjectEntry(tag), (MappingNode) valueNode);
            }
        }
        
    }
    
    public class ConstructEntry extends AbstractConstruct {
        
        @Override
        public Object construct(Node node) {
            return switch (node.getNodeId()) {
                case scalar -> constructScalar(node);
                case mapping -> constructMappingObject(node);
                case sequence -> constructSequence(node);
                case anchor -> constructAnchor(node);
            };
        }
        
        protected ConfigEntry constructSequence(Node valueNode) {
            ArrayEntry arrayEntry = new ArrayEntry(valueNode.getTag() == CfgTag.SEQ.yamlTag() ? null
                    : CfgTag.by(valueNode.getTag()).equals(ArrayEntry.SEQ_I) ? CfgTag.SEQ
                    : CfgTag.by(valueNode.getTag()));
            SequenceNode sequenceNode = (SequenceNode) valueNode;
            for (var element : sequenceNode.getValue())
                arrayEntry.add((ConfigEntry) construct(element));
            
            return arrayEntry;
        }
        
        protected ConfigEntry constructMappingObject(Node valueNode) {
            if (valueNode.getTag() == null || valueNode.getTag() == Tag.MAP || valueNode.getTag().equals(COMPOUND_CFG_TAG.yamlTag())) {
                return constructMapping(new CompoundEntry(), (MappingNode) valueNode);
            } else
                return new ObjectEntry(CfgTag.by(valueNode.getTag()), ConstructorExt.this.constructObject(valueNode));
        }
    
        protected CompoundEntry constructMapping(CompoundEntry map, MappingNode mnode) {
            for (NodeTuple tuple : mnode.getValue()) {
                var keyNode = tuple.getKeyNode();
                String key = ConstructorExt.this.constructObject(tuple.getKeyNode()).toString();
                ConfigEntry value = (ConfigEntry) construct(tuple.getValueNode());
                map.put(key, value);
                
                if (keyNode.getBlockComments() != null)
                    value.setBlockComment(keyNode.getBlockComments().stream().map(l -> l.getValue().substring(1)).collect(Collectors.joining("\n")));
                if (keyNode.getInLineComments() != null)
                    value.setInlineComment(keyNode.getInLineComments().stream().map(l -> l.getValue().substring(1)).collect(Collectors.joining("\n")));
            }
            return map;
        }
        
        protected ScalarEntry constructScalar(Node valueNode) {
            var factory = CompoundEntry.SCALAR_FACTORIES.get(CfgTag.by(valueNode.getTag()));
            ScalarEntry entry;
            
            if (factory == null) {
                if (valueNode.getTag().getValue().endsWith(CfgTag.ENUM_POSTFIX)) {
                    valueNode.setTag(new Tag(valueNode.getTag().getValue().replace(".enum", "")));
                    entry = new EnumEntry((Enum<?>) ConstructorExt.this.constructObject(valueNode));
                } else
                    throw new YAMLException("Unsupported scalar node tag in compound: " + valueNode.getTag());
            } else
                entry = factory.create(ConstructorExt.this.constructObject(valueNode));
            
            return entry;
        }
        
        protected ConfigEntry constructAnchor(Node valueNode) {
            throw new UnsupportedOperationException();
        }
        
    }
    
    public class ConstructSequenceCustomizable extends ConstructSequence {
        
        @Override
        public Object construct(Node node) {
            var construct = typeConstructorsMap.get(node.getType());
            if (construct != null)
                return construct.construct(node);
            return super.construct(node);
        }
        
    }
    
    public class ConstructMappingCustomized extends ConstructMapping {
        
        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            var construct = typeConstructorsMap.get(node.getType());
            
            if (construct != null)
                return construct.construct(node);
            
            return super.construct(mnode);
        }
        
        @Override
        public void construct2ndStep(Node node, Object object) {
            var construct = typeConstructorsMap.get(node.getType());
    
            if (construct != null) {
                construct.construct2ndStep(node, object);
                return;
            }
    
            super.construct2ndStep(node, object);
        }
        
    }
    
}
