package com.timreset.arduino.library.IRremote;

/**
 * @author Tim
 * @date 30.11.2015
 */
public class decode_results {
	public int decode_type; // NEC, SONY, RC5, UNKNOWN
	public int panasonicAddress; // This is only used for decoding Panasonic data
	public long value; // Decoded value
	public int bits; // Number of bits in decoded value
	public int rawbuf; // Raw intervals in .5 us ticks
	public int rawlen; // Number of records in rawbuf.

}
