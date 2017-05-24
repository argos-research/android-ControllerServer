package utils;

/**
 * This class is almost 1 to 1 representation of the input.h file from 
 * linux/input.h for easier interaction with the C environment.
 * @author Konstantin Vankov
 *
 */
public class uInputValuesHolder {

/*
 * Keys and buttons
 *
 * Most of the keys/buttons are modeled after USB HUT 1.12
 * (see http://www.usb.org/developers/hidpage).
 * Abbreviations in the comments:
 * AC - Application Control
 * AL - Application Launch Button
 * SC - System Control
 */

public static final int  KEY_RESERVED	= 0;
public static final int  KEY_ESC		= 1;
public static final int  KEY_1			= 2;
public static final int  KEY_2			= 3;
public static final int  KEY_3			= 4;
public static final int  KEY_4			= 5;
public static final int  KEY_5			= 6;
public static final int  KEY_6			= 7;
public static final int  KEY_7			= 8;
public static final int  KEY_8			= 9;
public static final int  KEY_9			= 10;
public static final int  KEY_0			= 11;
public static final int  KEY_MINUS		= 12;
public static final int  KEY_EQUAL		= 13;
public static final int  KEY_BACKSPACE	= 14;
public static final int  KEY_TAB		= 15;
public static final int  KEY_Q			= 16;
public static final int  KEY_W			= 17;
public static final int  KEY_E			= 18;
public static final int  KEY_R			= 19;
public static final int  KEY_T			= 20;
public static final int  KEY_Y			= 21;
public static final int  KEY_U			= 22;
public static final int  KEY_I			= 23;
public static final int  KEY_O			= 24;
public static final int  KEY_P			= 25;
public static final int  KEY_LEFTBRACE	= 26;
public static final int  KEY_RIGHTBRACE	= 27;
public static final int  KEY_ENTER		= 28;
public static final int  KEY_LEFTCTRL	= 29;
public static final int  KEY_A			= 30;
public static final int  KEY_S			= 31;
public static final int  KEY_D			= 32;
public static final int  KEY_F			= 33;
public static final int  KEY_G			= 34;
public static final int  KEY_H			= 35;
public static final int  KEY_J			= 36;
public static final int  KEY_K			= 37;
public static final int  KEY_L			= 38;
public static final int  KEY_SEMICOLON	= 39;
public static final int  KEY_APOSTROPHE	= 40;
public static final int  KEY_GRAVE		= 41;
public static final int  KEY_LEFTSHIFT	= 42;
public static final int  KEY_BACKSLASH	= 43;
public static final int  KEY_Z			= 44;
public static final int  KEY_X			= 45;
public static final int  KEY_C			= 46;
public static final int  KEY_V			= 47;
public static final int  KEY_B			= 48;
public static final int  KEY_N			= 49;
public static final int  KEY_M			= 50;
public static final int  KEY_COMMA		= 51;
public static final int  KEY_DOT		= 52;
public static final int  KEY_SLASH		= 53;
public static final int  KEY_RIGHTSHIFT	= 54;
public static final int  KEY_KPASTERISK	= 55;
public static final int  KEY_LEFTALT	= 56;
public static final int  KEY_SPACE		= 57;
public static final int  KEY_CAPSLOCK	= 58;
public static final int  KEY_F1			= 59;
public static final int  KEY_F2			= 60;
public static final int  KEY_F3			= 61;
public static final int  KEY_F4			= 62;
public static final int  KEY_F5			= 63;
public static final int  KEY_F6			= 64;
public static final int  KEY_F7			= 65;
public static final int  KEY_F8			= 66;
public static final int  KEY_F9			= 67;
public static final int  KEY_F10		= 68;
public static final int  KEY_NUMLOCK	= 69;
public static final int  KEY_SCROLLLOCK	= 70;
public static final int  KEY_KP7		= 71;
public static final int  KEY_KP8		= 72;
public static final int  KEY_KP9		= 73;
public static final int  KEY_KPMINUS	= 74;
public static final int  KEY_KP4		= 75;
public static final int  KEY_KP5		= 76;
public static final int  KEY_KP6		= 77;
public static final int  KEY_KPPLUS		= 78;
public static final int  KEY_KP1		= 79;
public static final int  KEY_KP2		= 80;
public static final int  KEY_KP3		= 81;
public static final int  KEY_KP0		= 82;
public static final int  KEY_KPDOT		= 83;

public static final int  KEY_102ND		= 86;
public static final int  KEY_F11		= 87;
public static final int  KEY_F12		= 88;
public static final int  KEY_RO			= 89;
public static final int  KEY_KATAKANA	= 90;
public static final int  KEY_HIRAGANA	= 91;
public static final int  KEY_HENKAN		= 92;
public static final int  KEY_KATAKANAHIRAGANA= 93;
public static final int  KEY_MUHENKAN	= 94;
public static final int  KEY_KPJPCOMMA	= 95;
public static final int  KEY_KPENTER	= 96;
public static final int  KEY_RIGHTCTRL	= 97;
public static final int  KEY_KPSLASH	= 98;
public static final int  KEY_SYSRQ		= 99;
public static final int  KEY_RIGHTALT	= 100;
public static final int  KEY_LINEFEED	= 101;
public static final int  KEY_HOME		= 102;
public static final int  KEY_UP			= 103;
public static final int  KEY_PAGEUP		= 104;
public static final int  KEY_LEFT		= 105;
public static final int  KEY_RIGHT		= 106;
public static final int  KEY_END		= 107;
public static final int  KEY_DOWN		= 108;
public static final int  KEY_PAGEDOWN	= 109;
public static final int  KEY_INSERT		= 110;
public static final int  KEY_DELETE		= 111;
public static final int  KEY_MACRO		= 112;
public static final int  KEY_MUTE		= 113;
public static final int  KEY_VOLUMEDOWN = 114;
public static final int  KEY_VOLUMEUP	= 115;
public static final int  KEY_POWER		= 116;	/* SC System Power Down */
public static final int  KEY_KPEQUAL	= 117;
public static final int  KEY_KPPLUSMINUS= 118;
public static final int  KEY_PAUSE		= 119;
public static final int  KEY_SCALE		= 120;	/* AL Compiz Scale (Expose) */
}
