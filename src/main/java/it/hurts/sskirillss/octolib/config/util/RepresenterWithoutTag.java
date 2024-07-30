package it.hurts.sskirillss.octolib.config.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RepresenterWithoutTag extends RepresenterExt {
    
    public RepresenterWithoutTag(DumperOptions options) {
        super(options);
    }
    
    @Override
    protected boolean shouldConvertEnumToStr() {
        return true;
    }
    
    protected Node representSequence(Tag tag, Iterable<?> sequence, DumperOptions.FlowStyle flowStyle) {
        int size = 10;
        if (sequence instanceof List) {
            size = ((List)sequence).size();
        }
        
        List<Node> value = new ArrayList<>(size);
        SequenceNode node = new SequenceNode(Tag.SEQ, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        DumperOptions.FlowStyle bestStyle = DumperOptions.FlowStyle.FLOW;
        
        Node nodeItem;
        for(Iterator<?> var8 = sequence.iterator(); var8.hasNext(); value.add(nodeItem)) {
            Object item = var8.next();
            nodeItem = this.representData(item);
            if (!(nodeItem instanceof ScalarNode) || !((ScalarNode)nodeItem).isPlain()) {
                bestStyle = DumperOptions.FlowStyle.BLOCK;
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
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        if (!classTags.containsKey(javaBean.getClass()))
            addClassTag(javaBean.getClass(), Tag.MAP);

        return super.representJavaBean(properties, javaBean);
    }
    
}
