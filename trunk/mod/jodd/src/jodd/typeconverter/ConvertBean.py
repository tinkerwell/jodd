
f = open('ConvertBean.java', 'r')
java = f.read()
f.close()

genStart = java.find('@@generated')
java = java[0:genStart + 11]

### -----------------------------------------------------------------

types = [
	[0, 'Boolean', 'boolean', 'false'],
	[2, 'Integer', 'int', '0'],
	[4, 'Long', 'long', '0'],
	[6, 'Float', 'float', '0'],
	[8, 'Double', 'double', '0'],
	[10, 'Short', 'short', '(short) 0'],
	[12, 'Character', 'char', '(char) 0'],
]

template = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public $T to$T(Object value) {
		return ($T) typeConverters[#].convert(value);
	}

	/**
	 * Converts value to <code>$t</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public $t to$T(Object value, $t defaultValue) {
		$T result = ($T) typeConverters[#++].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.$tValue();
	}

	/**
	 * Converts value to <code>$t</code> with common default value.
	 */
	public $t to$TValue(Object value) {
		return to$T(value, $D);
	}
'''

for type in types:
	# small type
	data = template
	data = data.replace('#++', str(type[0] + 1))
	data = data.replace('#', str(type[0]))
	data = data.replace('$T', type[1])
	data = data.replace('$t', type[2])
	data = data.replace('$D', type[3])
	java += data

### -----------------------------------------------------------------

types = [
	[14, 'boolean[]', 'BooleanArray'],
	[15, 'int[]', 'IntegerArray'],
	[16, 'long[]', 'LongArray'],
	[17, 'float[]', 'FloatArray'],
	[18, 'double[]', 'DoubleArray'],
	[19, 'short[]', 'ShortArray'],
	[20, 'char[]', 'CharacterArray'],
	[21, 'String', 'String'],
	[22, 'String[]', 'StringArray'],
	[23, 'Class', 'Class'],
	[24, 'Class[]', 'ClassArray'],
	[25, 'JDateTime', 'JDateTime'],
	[26, 'Date', 'Date'],
	[27, 'Calendar', 'Calendar'],
	[28, 'BigInteger', 'BigInteger'],
	[29, 'BigDecimal', 'BigDecimal'],
]

template = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public $T to$N(Object value) {
		return ($T) typeConverters[#].convert(value);
	}
'''

for type in types:
	# small type
	data = template
	data = data.replace('#', str(type[0]))
	data = data.replace('$T', type[1])
	data = data.replace('$N', type[2])
	java += data


### -----------------------------------------------------------------

java += '}'

f = open('ConvertBean.java', 'w')
f.write(java)
f.close()