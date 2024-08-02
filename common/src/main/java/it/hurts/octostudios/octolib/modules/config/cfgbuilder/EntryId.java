package it.hurts.octostudios.octolib.modules.config.cfgbuilder;

import org.yaml.snakeyaml.nodes.NodeId;

public enum EntryId {
    
    SCALAR(NodeId.scalar),
    MAPPING(NodeId.mapping),
    OBJECT(NodeId.mapping),
    SEQUENCE(NodeId.sequence),
    ANCHOR(NodeId.anchor);
//    ENUM(NodeId.mapping);
    
    final NodeId id;
    
    EntryId(NodeId id) {
        this.id = id;
    }
    
    public NodeId nodeId() {
        return id;
    }
    
}
