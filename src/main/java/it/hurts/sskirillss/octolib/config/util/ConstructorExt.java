package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.annotations.CfgConstructor;
import it.hurts.sskirillss.octolib.config.cfgbuilder.*;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.ScalarEntry;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.BaseConstructor;
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
        
        typeConstructorsMap.put(CompoundEntry.class, new ConstructEntry());
        typeConstructorsMap.put(DeconstructedObjectEntry.class, new ConstructMappedEntry());
        yamlConstructors.put(COMPOUND_CFG_TAG.yamlTag(), new ConstructEntry());
        yamlConstructors.put(DECONSTRUCTED_CFG_TAG.yamlTag(), new ConstructMappedEntry());
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
        
        protected ConfigEntry constructMapping(Node valueNode) {
            MappingNode mappingNode = (MappingNode) valueNode;
            if (valueNode.getTag() == null || valueNode.getTag() == Tag.MAP) {
                valueNode.setTag(DECONSTRUCTED_CFG_TAG.yamlTag());
                return (CompoundEntry) (ConstructorExt.this.constructObject(valueNode));
            } else {
                var tag = CfgTag.by(mappingNode.getTag());
                mappingNode.setTag(Tag.MAP);
                mappingNode.setType(DeconstructedObjectEntry.class);
                return new DeconstructedObjectEntry(tag, (CompoundEntry) (ConstructorExt.this.constructObject(valueNode)));
            }
        }
        
    }
    
    public class ConstructEntry extends AbstractConstruct {
        
        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            CompoundEntry compound = new CompoundEntry();
            
            for (NodeTuple tuple : mnode.getValue()) {
                var keyNode = tuple.getKeyNode();
                String key = ConstructorExt.this.constructObject(tuple.getKeyNode()).toString();
                ConfigEntry value = constructValue(tuple.getValueNode());
                compound.put(key, value);
                
                if (keyNode.getBlockComments() != null)
                    value.setBlockComment(keyNode.getBlockComments().stream().map(CommentLine::getValue).collect(Collectors.joining("\n")));
                if (keyNode.getInLineComments() != null)
                    value.setInlineComment(keyNode.getInLineComments().stream().map(CommentLine::getValue).collect(Collectors.joining("\n")));
            }
            
            return compound;
        }
        
        protected ConfigEntry constructValue(Node valueNode) {
            return switch (valueNode.getNodeId()) {
                case scalar -> constructScalar(valueNode);
                case mapping -> constructMapping(valueNode);
                case sequence -> constructSequence(valueNode);
                case anchor -> constructAnchor(valueNode);
            };
        }
        
        protected ConfigEntry constructSequence(Node valueNode) {
            ArrayEntry arrayEntry = new ArrayEntry(valueNode.getTag() == CfgTag.SEQ.yamlTag() ? null
                    : CfgTag.by(valueNode.getTag()).equals(ArrayEntry.SEQ_I) ? CfgTag.SEQ
                    : CfgTag.by(valueNode.getTag()));
            SequenceNode sequenceNode = (SequenceNode) valueNode;
            for (var element : sequenceNode.getValue())
                arrayEntry.add(constructValue(element));
            
            return arrayEntry;
        }
        
        protected ConfigEntry constructMapping(Node valueNode) {
            if (valueNode.getTag() == null || valueNode.getTag() == Tag.MAP) {
                valueNode.setTag(COMPOUND_CFG_TAG.yamlTag());
                return (CompoundEntry) (ConstructorExt.this.constructObject(valueNode));
            } else
                return new ObjectEntry(CfgTag.by(valueNode.getTag()), ConstructorExt.this.constructObject(valueNode));
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
    
    public class ConstructMappingCustomized extends ConstructMapping {
        
        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode) node;
            if (Map.class.isAssignableFrom(node.getType())) {
                return node.isTwoStepsConstruction() ? ConstructorExt.this.newMap(mnode) : ConstructorExt.this.constructMapping(mnode);
            } else if (Collection.class.isAssignableFrom(node.getType())) {
                return node.isTwoStepsConstruction() ? ConstructorExt.this.newSet(mnode) : ConstructorExt.this.constructSet(mnode);
            } else {
                var construct = typeConstructorsMap.get(node.getType());
                if (construct == null) {
                    Object obj = ConstructorExt.this.newInstance(mnode);
                    if (obj != BaseConstructor.NOT_INSTANTIATED_OBJECT) {
                        return node.isTwoStepsConstruction() ? obj : this.constructJavaBean2ndStep(mnode, obj);
                    } else {
                        throw new YAMLException("Can't create an instance for " + mnode.getTag());
                    }
                }
                
                return construct.construct(node);
            }
        }
        
        @Override
        public void construct2ndStep(Node node, Object object) {
            if (Map.class.isAssignableFrom(node.getType())) {
                ConstructorExt.this.constructMapping2ndStep((MappingNode) node, (Map) object);
            } else if (Set.class.isAssignableFrom(node.getType())) {
                ConstructorExt.this.constructSet2ndStep((MappingNode) node, (Set) object);
            } else {
                var construct = typeConstructorsMap.get(node.getType());
                
                if (construct == null)
                    this.constructJavaBean2ndStep((MappingNode) node, object);
                else
                    construct.construct2ndStep(node, object);
            }
            
        }
        
    }
    
}
