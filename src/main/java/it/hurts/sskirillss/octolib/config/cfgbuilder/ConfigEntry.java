package it.hurts.sskirillss.octolib.config.cfgbuilder;

import it.hurts.sskirillss.octolib.config.annotations.TypePropInherited;

import java.util.Arrays;
import java.util.stream.Collectors;

@TypePropInherited
public abstract class ConfigEntry {
    
    private String inlineComment;
    private String blockComment;
    protected CfgTag tag;
    private final EntryId nodeId;
    protected CfgTag type;
    
    public ConfigEntry(CfgTag tag, EntryId nodeId) {
        if (tag == null)
            throw new IllegalArgumentException("Tag in a Entry is required.");
        this.tag = tag;
        this.nodeId = nodeId;
    }
    
    public abstract Object getData();
    
    public boolean isNull() {
        return getData() == null;
    }
    
    public CfgTag getTag() {
        return tag;
    }
    
    public void setType(CfgTag type) {
        this.type = type;
    }
    
    public CfgTag getType() {
        return type;
    }
    
    public EntryId getNodeId() {
        return nodeId;
    }
    
    public String getInlineComment() {
        return inlineComment;
    }
    
    public void setInlineComment(String inlineComment) {
        this.inlineComment = inlineComment;
    }
    
    public String getBlockComment() {
        return blockComment;
    }
    
    public void setBlockComment(String blockComment) {
        this.blockComment = blockComment;
    }
    
    protected String formatInline() {
        return inlineComment == null ? "" : Arrays.stream(inlineComment.split("\n")).map(s -> "<# " + s + ">").collect(Collectors.joining("\n"));
    }
    
    protected String formatBlock() {
        return blockComment == null ? "" : Arrays.stream(blockComment.split("\n")).map(s -> "<# " + s + ">").collect(Collectors.joining("\n"));
    }
    
    public ConfigEntry refactor(ConfigEntry entry) {
        return entry;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(formatBlock());
        if (!builder.isEmpty())
            builder.append("\n");
        return builder.append(getClass().getSimpleName())
                .append("||")
                .append(getData())
                .append(formatInline()).toString();
    }
    
}
