package it.hurts.octostudios.octolib.modules.config.cfgbuilder;

import org.yaml.snakeyaml.nodes.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class ArrayEntry extends ConfigEntry implements Iterable<ConfigEntry> {
    
    public static final CfgTag SEQ_I = new CfgTag(Tag.PREFIX + "seq_seq");
    
    public ArrayEntry() {
        this((CfgTag) null);
    }
    
    public ArrayEntry(CfgTag tag) {
        super(CfgTag.SEQ, EntryId.SEQUENCE);
        this.type = tag;
    }
    
    public ArrayEntry(Collection<? extends ConfigEntry> entries) {
        this(null, entries);
    }
    
    public ArrayEntry(CfgTag tag, Collection<? extends ConfigEntry> entries) {
        super(CfgTag.SEQ, EntryId.SEQUENCE);
        this.list.addAll(entries);
        this.type = tag;
    }
    
    public <T extends ConfigEntry> ArrayEntry(T... entries) {
        this(null, entries);
    }
    
    public <T extends ConfigEntry> ArrayEntry(CfgTag tag, T... entries) {
        super(CfgTag.SEQ, EntryId.SEQUENCE);
        this.list.addAll(List.of(entries));
        this.type = tag;
    }
    
    List<ConfigEntry> list = new ArrayList<>();
    
    public void remove() {
        if (list.isEmpty())
            throw new IllegalStateException("Array entry is empty");
        list.remove(list.size() - 1);
    }
    
    public void add(ConfigEntry entry) {
        if (getType() != null && entry.getTag() != getType())
            throw new IllegalArgumentException(String.format("Illegal tag type: entry with tag %s cannot be added in list with tag %s.", entry.getTag(), getType()));
        list.add(entry);
    }
    
    public void add(int index, ConfigEntry entry) {
        if (getType() != null && entry.getTag() != getType())
            throw new IllegalArgumentException(String.format("Illegal tag type: entry with tag %s cannot be added in list with tag %s.", entry.getTag(), getType()));
        list.add(index, entry);
    }
    
    public int size() {
        return list.size();
    }
    
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    @Override
    public Object getData() {
        return list;
    }
    
    @Override
    public Iterator<ConfigEntry> iterator() {
        return list.iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ArrayEntry");
        builder.append(getType() == null ? " " : "!!" + getType()).append("[\n");
        
        var iterator = list.iterator();
        while (iterator.hasNext()) {
            var value = iterator.next();
            builder.append(Arrays.stream((value.getData() + (iterator.hasNext() ? "," : "")).split("\n"))
                    .map(ss -> "\t" + ss + "\n")
                    .collect(Collectors.joining()));
        }
        builder.append("]");
        return builder.toString();
    }
    
}
