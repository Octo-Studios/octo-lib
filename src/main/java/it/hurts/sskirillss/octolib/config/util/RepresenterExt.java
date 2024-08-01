package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.annotations.TypeProp;
import it.hurts.sskirillss.octolib.config.annotations.TypePropInherited;
import it.hurts.sskirillss.octolib.config.cfgbuilder.*;
import it.hurts.sskirillss.octolib.config.util.properties.GenericPropertyExt;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;

public class RepresenterExt extends Representer {
    
    public RepresenterExt(DumperOptions options) {
        super(options);
        multiRepresenters.put(Deque.class, new RepresentDeque());
        multiRepresenters.put(ConfigEntry.class, new RepresentEntry());
        representers.put(CompoundEntry.class, new RepresentEntry());
        representers.put(DeconstructedObjectEntry.class, new RepresentEntry());
    }
    
    @Override
    protected Node representMapping(Tag tag, Map<?, ?> mapping, DumperOptions.FlowStyle flowStyle) {
        List<NodeTuple> value = new ArrayList<>(mapping.size());
        MappingNode node = new MappingNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
    
        Node nodeKey;
        Node nodeValue;
        for(Iterator<?> var7 = mapping.entrySet().iterator(); var7.hasNext(); value.add(new NodeTuple(nodeKey, nodeValue))) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)var7.next();
            nodeKey = this.representData(entry.getKey());
            nodeValue = this.representData(entry.getValue());
            if (!(nodeKey instanceof ScalarNode) || !((ScalarNode)nodeKey).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
        
            if (!(nodeValue instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
            }
    
            if (nodeValue.getNodeId() == NodeId.mapping) {
                if (nodeValue.getBlockComments() != null) {
                    nodeKey.setBlockComments(nodeValue.getBlockComments());
                    nodeValue.setBlockComments(null);
                }
        
                if (nodeValue.getInLineComments() != null) {
                    nodeKey.setBlockComments(nodeValue.getInLineComments());
                    nodeValue.setInLineComments(null);
                }
            }
        }
    
        if (flowStyle == DumperOptions.FlowStyle.AUTO) {
            if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
                node.setFlowStyle(this.defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
    
        return node;
    }
    
    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        ScalarNode nodeKey = (ScalarNode) this.representData(property.getName());
        boolean hasAlias = this.representedObjects.containsKey(propertyValue);
        
        Node nodeValue = this.representData(propertyValue);
        configureCommentsProperty(nodeKey, nodeValue, javaBean, property);
        
        if (propertyValue != null && !hasAlias) {
            NodeId nodeId = nodeValue.getNodeId();
            if (customTag == null) {
                if (nodeId == NodeId.scalar) {
                    if (property.getType() != Enum.class && propertyValue instanceof Enum) {
                        if (convertEnumToStr())
                            nodeValue.setTag(Tag.STR);
                        else
                            nodeValue.setTag(new Tag(nodeValue.getTag().getValue() + CfgTag.ENUM_POSTFIX));
                    }
                } else {
                    if (nodeId == NodeId.mapping && property.getType() == propertyValue.getClass() && !(propertyValue instanceof Map) && !nodeValue.getTag().equals(Tag.SET))
                        nodeValue.setTag(Tag.MAP);
                    
                    this.checkGlobalTag(property, nodeValue, propertyValue);
                }
            }
        }
    
        if (nodeValue.getNodeId() == NodeId.mapping) {
            if (nodeValue.getBlockComments() != null) {
                nodeKey.setBlockComments(nodeValue.getBlockComments());
                nodeValue.setBlockComments(null);
            }
        
            if (nodeValue.getInLineComments() != null) {
                nodeKey.setBlockComments(nodeValue.getInLineComments());
                nodeValue.setInLineComments(null);
            }
        }
        
        return new NodeTuple(nodeKey, nodeValue);
    }
    
    protected boolean convertEnumToStr() {
        return false;
    }
    
    protected boolean removeTypes() {
        return false;
    }
    
    @Override
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        if (removeTypes() && !classTags.containsKey(javaBean.getClass()))
            addClassTag(javaBean.getClass(), Tag.MAP);
        
        var node = super.representJavaBean(properties, javaBean);
        configureCommentsJavaBean(node, javaBean);
        return node;
    }
    
    protected void configureCommentsJavaBean(Node nodeValue, Object javaBean) {
        TypeSettings settings = null;
        if (javaBean != null) {
            
            if (javaBean.getClass().isAnnotationPresent(TypePropInherited.class))
                settings = new TypeSettings(javaBean.getClass().getAnnotation(TypePropInherited.class));
            if (javaBean.getClass().isAnnotationPresent(TypeProp.class))
                settings = new TypeSettings(javaBean.getClass().getAnnotation(TypeProp.class));
            
            if (settings == null)
                return;
            
            List<CommentLine> block = nodeValue.getBlockComments() == null ? new ArrayList<>() : nodeValue.getBlockComments();
            if (!settings.comment.isEmpty())
                block.addAll(parseStringToComment(settings.comment, CommentType.BLOCK));
            if (!block.isEmpty())
                nodeValue.setBlockComments(block);
            
            List<CommentLine> inline = nodeValue.getInLineComments() == null ? new ArrayList<>() : nodeValue.getInLineComments();
            if (!settings.inlineComment.isEmpty())
                inline.addAll(parseStringToComment(settings.inlineComment, CommentType.IN_LINE));
            if (!inline.isEmpty())
                nodeValue.setInLineComments(inline);
            
        }
    }
    
    protected void configureCommentsProperty(Node nodeKey, Node nodeValue, Object javaBean, Property property) {
        if (property instanceof GenericPropertyExt extended) {
            List<CommentLine> block = nodeKey.getBlockComments() == null ? new ArrayList<>() : nodeKey.getBlockComments();
            if (extended.getBlockComment() != null)
                block.addAll(parseStringToComment(extended.getBlockComment(), CommentType.BLOCK));
            if (!block.isEmpty())
                nodeKey.setBlockComments(block);
            
            List<CommentLine> inline = nodeKey.getInLineComments() == null ? new ArrayList<>() : nodeKey.getInLineComments();
            if (extended.getInlineComment() != null)
                inline.addAll(parseStringToComment(extended.getInlineComment(), CommentType.IN_LINE));
            if (!inline.isEmpty())
                nodeKey.setInLineComments(inline);
        }
        
    }
    
    protected List<CommentLine> parseStringToComment(String comment, CommentType type) {
        return Arrays.stream(comment.split("\n")).map(s ->
                new CommentLine(null, null, " " + s, type)).toList();
    }
    
    @Override
    protected Set<Property> getProperties(Class<?> type) {
        return this.typeDefinitions.containsKey(type) ? this.typeDefinitions.get(type).getProperties()
                : this.getPropertyUtils().getProperties(type,
                type.getAnnotation(TypePropInherited.class) == null ? BeanAccess.FIELD : type.getAnnotation(TypePropInherited.class).accessType());
    }
    
    public static class TypeSettings {
        
        final String inlineComment;
        final String comment;
        final BeanAccess accessType;
        final boolean onlyProps;
        
        public TypeSettings(TypePropInherited annotation) {
            this.inlineComment = annotation.inlineComment();
            this.comment = annotation.comment();
            this.accessType = annotation.accessType();
            this.onlyProps = annotation.onlyProps();
        }
        
        public TypeSettings(TypeProp annotation) {
            this.inlineComment = annotation.inlineComment();
            this.comment = annotation.comment();
            this.accessType = annotation.accessType();
            this.onlyProps = annotation.onlyProps();
        }
        
    }
    
    protected class RepresentEntry implements Represent {
        
        @Override
        public Node representData(Object o) {
            var entry = (ConfigEntry) o;
            var node = switch (entry.getNodeId()) {
                case SCALAR -> {
                    try {
                        yield representScalar(entry.getTag().yamlTag(), String.valueOf(entry.getData()));
                    } catch (ClassCastException exception) {
                        throw new YAMLException("Config Entry of scalar node id must have String type data.", exception);
                    }
                }
                case MAPPING, OBJECT -> {
                    var n = RepresenterExt.this.representData(entry.getData());
                    if (!removeTypes() && entry.getTag() != CfgTag.MAP && entry.getTag() != null && entry.getTag() != CompoundEntry.COMPOUND_CFG_TAG)
                        n.setTag(entry.getTag().yamlTag());
                    yield n;
                }
                case SEQUENCE -> {
                    try {
                        yield representSequence(entry.getType() == null ? Tag.SEQ
                                : entry.getType().yamlTag() == Tag.SEQ ? ArrayEntry.SEQ_I.yamlTag()
                                : entry.getType().yamlTag(), (Iterable<?>) entry.getData(), DumperOptions.FlowStyle.BLOCK);
                    } catch (ClassCastException exception) {
                        throw new YAMLException("Config Entry of scalar node id must have Iterable type data.", exception);
                    }
                }
                case ANCHOR -> throw new UnsupportedOperationException();
            };
            
            if (entry.getBlockComment() != null && !entry.getBlockComment().isEmpty()) {
                var blockComments = new ArrayList<>(parseStringToComment(entry.getBlockComment(), CommentType.BLOCK));
                if (node.getBlockComments() != null)
                    blockComments.addAll(node.getBlockComments());
                node.setBlockComments(blockComments);
            }
            
            if (entry.getInlineComment() != null && !entry.getInlineComment().isEmpty()) {
                var inlineComments = new ArrayList<>(parseStringToComment(entry.getInlineComment(), CommentType.BLOCK));
                if (node.getInLineComments() != null)
                    inlineComments.addAll(node.getInLineComments());
                node.setInLineComments(inlineComments);
            }
            
            return node;
        }
        
    }
    
    protected class RepresentDeque implements Represent {
        protected RepresentDeque() {
        }
        
        public Node representData(Object data) {
            return representSequence(getTag(data.getClass(), Tag.SEQ), (Deque)data, DumperOptions.FlowStyle.AUTO);
        }
    }
    
}
