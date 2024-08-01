package it.hurts.sskirillss.octolib.config.cfgbuilder;

import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.*;

import java.util.*;
import java.util.stream.Collectors;

public class CompoundEntry extends ConfigEntry implements Iterable<Map.Entry<String, ConfigEntry>> {
    
    public static final CfgTag COMPOUND_CFG_TAG = new CfgTag(CompoundEntry.class);
    
    public static final Map<CfgTag, ScalarFactory> SCALAR_FACTORIES = new HashMap<>() {{
        put(CfgTag.INT, o -> new IntEntry((Integer) o));
        put(CfgTag.STR, o -> new StringEntry((String) o));
        put(CfgTag.FLOAT, o -> new DoubleEntry((Double) o));
        put(CfgTag.BOOL, o -> new BoolEntry((Boolean) o));
        put(CfgTag.BINARY, o -> new BinaryEntry((Byte) o));
        put(CfgTag.NULL, o -> new NullEntry());
    }};
    
    protected LinkedHashMap<String, ConfigEntry> map = new LinkedHashMap<>();
    
    public CompoundEntry() {
        super(CfgTag.MAP, EntryId.MAPPING);
    }
    
    public CompoundEntry(Map<String, ConfigEntry> map) {
        super(CfgTag.MAP, EntryId.MAPPING);
        this.map.putAll(map);
    }
    
    protected CompoundEntry(CfgTag tag) {
        super(tag, EntryId.MAPPING);
    }
    
    @Override
    public Object getData() {
        return map;
    }
    
    public void put(String key, ConfigEntry entry) {
        map.put(key, entry);
    }
    
    public void putByte(String key, byte value) {
        map.put(key, new BinaryEntry(value));
    }
    
    public void putString(String key, String value) {
        map.put(key, new StringEntry(value));
    }
    
    public void putBoolean(String key, boolean value) {
        map.put(key, new BoolEntry(value));
    }
    
    public void putObject(String key, Object value) {
        map.put(key, new ObjectEntry(value));
    }
    
    public void putObjectList(String key, Object... list) {
        putObjectList(key, List.of(list));
    }
    
    public void putObjectList(String key, List<Object> list) {
        ArrayEntry entry = new ArrayEntry(list.stream().map(ObjectEntry::new).toList());
        map.put(key, entry);
    }
    
    public void putScalarList(String key, CfgTag scalarTag, Object... list) {
        putScalarList(key, scalarTag, List.of(list));
    }
    
    public void putScalarList(String key, CfgTag scalarTag, List<Object> list) {
        ArrayEntry entry = new ArrayEntry(scalarTag);
        
        if (!SCALAR_FACTORIES.containsKey(scalarTag))
            throw new IllegalArgumentException("Unsupported scalar tag: " + scalarTag);
        
        var factory = SCALAR_FACTORIES.get(scalarTag);
        for (var element : list) {
            try {
                entry.add(factory.create(element));
            } catch (ClassCastException exception) {
                throw new IllegalArgumentException("Element cannot be cast to " + scalarTag);
            }
        }
        map.put(key, entry);
    }
    
    public void putList(String key, ArrayEntry list) {
        map.put(key, list);
    }
    
    public void putList(String key, List<ConfigEntry> list) {
        map.put(key, new ArrayEntry(list));
    }
    
    public void putInt(String key, int value) {
        map.put(key, new IntEntry(value));
    }
    
    public void putNull(String key) {
        map.put(key, new NullEntry());
    }
    
    public void putDouble(String key, double value) {
        map.put(key, new DoubleEntry(value));
    }
    
    public ConfigEntry get(String key) {
        return map.get(key);
    }
    
    public byte getByte(String key) {
        var entry = map.get(key);
        checkType(entry, CfgTag.BINARY);
        return (byte) entry.getData();
    }
    
    public ArrayEntry getList(String key) {
        var entry = map.get(key);
        checkType(entry, CfgTag.SEQ);
        return (ArrayEntry) entry;
    }
    
    public boolean getBoolean(String key, boolean type) {
        var entry = map.get(key);
        checkType(entry, CfgTag.BOOL);
        return (boolean) entry.getData();
    }
    
    public <T> T getObject(String key, Class<T> type) {
        var entry = map.get(key);
        checkType(entry, new CfgTag(type));
        return (T) entry.getData();
    }
    
    public String getString(String key) {
        var entry = map.get(key);
        checkType(entry, CfgTag.STR);
        return (String) entry.getData();
    }
    
    public double getDouble(String key) {
        var entry = map.get(key);
        checkType(entry, CfgTag.FLOAT);
        return (double) entry.getData();
    }
    
    public int getInt(String key) {
        var entry = map.get(key);
        checkType(entry, CfgTag.INT);
        return (int) entry.getData();
    }
    
    private void checkType(ConfigEntry entry, CfgTag tag) {
        if (entry.getType() != tag && !entry.getTag().equals(tag))
            throw new IllegalArgumentException(String.format("Incorrect tag type: %s is expected.", tag));
    }
    
    public CompoundEntry getWithoutTypes() {
        CompoundEntry compoundEntry = new CompoundEntry(map);
        compoundEntry.map.replaceAll((key, value) -> {
            var entry = switch (value.getNodeId()) {
                case SCALAR, ANCHOR -> value;
                case OBJECT -> new ObjectEntry(COMPOUND_CFG_TAG, value.getData());
                case SEQUENCE -> new ArrayEntry((Collection<? extends ConfigEntry>) value.getData());
                case MAPPING -> ((CompoundEntry) value).getWithoutTypes();
            };
            entry.setInlineComment(value.getInlineComment());
            entry.setBlockComment(value.getBlockComment());
            return entry;
        });
        return compoundEntry;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName().isEmpty()
                ? getClass().getSuperclass().getSimpleName() : getClass().getSimpleName());
        if (this.tag != CfgTag.MAP)
            builder.append("!!").append(tag);
        else
            builder.append(" ");
        builder.append("{\n");
        map.forEach((s, entry) -> builder.append(Arrays.stream((s + ": " + entry).split("\n"))
                .map(ss -> "\t" + ss + "\n")
                .collect(Collectors.joining())));
        builder.append("}");
        return builder.toString();
    }
    
    public Set<String> getKeys() {
        return map.keySet();
    }
    
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }
    
    @Override
    public Iterator<Map.Entry<String, ConfigEntry>> iterator() {
        return map.entrySet().iterator();
    }
    
    public interface ScalarFactory {
        
        ScalarEntry create(Object object);
        
    }
    
}
