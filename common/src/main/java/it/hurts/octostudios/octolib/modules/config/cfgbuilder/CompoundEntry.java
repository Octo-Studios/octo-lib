package it.hurts.octostudios.octolib.modules.config.cfgbuilder;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CompoundEntry extends ConfigEntry implements Map<String, ConfigEntry> {
    
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
        super(COMPOUND_CFG_TAG, EntryId.MAPPING);
    }
    
    public CompoundEntry(Map<String, ConfigEntry> map) {
        super(COMPOUND_CFG_TAG, EntryId.MAPPING);
        this.map.putAll(map);
    }
    
    protected CompoundEntry(CfgTag tag) {
        super(tag, EntryId.MAPPING);
    }
    
    @Override
    public Object getData() {
        return map;
    }
    
    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return false;
    }
    
    @Override
    public boolean containsValue(Object value) {
        return false;
    }
    
    @Override
    public ConfigEntry get(Object key) {
        return map.get(key);
    }
    
    public ConfigEntry put(String key, ConfigEntry entry) {
        map.put(key, entry);
        return entry;
    }
    
    @Override
    public ConfigEntry remove(Object key) {
        return map.remove(key);
    }
    
    @Override
    public void putAll(@NotNull Map<? extends String, ? extends ConfigEntry> m) {
        map.putAll(m);
    }
    
    @Override
    public void clear() {
        map.clear();
    }
    
    @NotNull
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }
    
    @NotNull
    @Override
    public Collection<ConfigEntry> values() {
        return map.values();
    }
    
    @NotNull
    @Override
    public Set<Entry<String, ConfigEntry>> entrySet() {
        return map.entrySet();
    }
    
    public BinaryEntry putByte(String key, byte value) {
        BinaryEntry entry = new BinaryEntry(value);
        map.put(key, new BinaryEntry(value));
        return entry;
    }
    
    public StringEntry putString(String key, String value) {
        var entry = new StringEntry(value);
        map.put(key, new StringEntry(value));
        return entry;
    }
    
    public BoolEntry putBoolean(String key, boolean value) {
        var entry = new BoolEntry(value);
        map.put(key, entry);
        return entry;
    }
    
    public ObjectEntry putObject(String key, Object value) {
        var entry = new ObjectEntry(value);
        map.put(key, entry);
        return entry;
    }
    
    public ArrayEntry putObjectList(String key, Object... list) {
        return putObjectList(key, List.of(list));
    }
    
    public ArrayEntry putObjectList(String key, List<Object> list) {
        ArrayEntry entry = new ArrayEntry(list.stream().map(ObjectEntry::new).toList());
        map.put(key, entry);
        return entry;
    }
    
    public ArrayEntry putScalarList(String key, CfgTag scalarTag, Object... list) {
        return putScalarList(key, scalarTag, List.of(list));
    }
    
    public ArrayEntry putScalarList(String key, CfgTag scalarTag, List<Object> list) {
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
        return entry;
    }
    
    public ArrayEntry putList(String key, ArrayEntry list) {
        map.put(key, list);
        return list;
    }
    
    public ArrayEntry putList(String key, List<ConfigEntry> list) {
        var entry = new ArrayEntry(list);
        map.put(key, new ArrayEntry(list));
        return entry;
    }
    
    public IntEntry putInt(String key, int value) {
        IntEntry entry = new IntEntry(value);
        map.put(key, entry);
        return entry;
    }
    
    public NullEntry putNull(String key) {
        var entry = new NullEntry();
        map.put(key, entry);
        return entry;
    }
    
    public DoubleEntry putDouble(String key, double value) {
        var entry = new DoubleEntry(value);
        map.put(key, entry);
        return entry;
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
    
    public boolean getBoolean(String key) {
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
        if (entry != null && entry.getType() != tag && !entry.getTag().equals(tag))
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
    
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }
    
    public interface ScalarFactory {
        
        ScalarEntry create(Object object);
        
    }
    
}
