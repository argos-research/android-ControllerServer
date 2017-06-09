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
	    
	    
	    /* Code 255 is reserved for special needs of AT keyboard driver */

	    public static final int BTN_MISC		= 0x100;
	    public static final int BTN_0			= 0x100;
	    public static final int BTN_1			= 0x101;
	    public static final int BTN_2			= 0x102;
	    public static final int BTN_3			= 0x103;
	    public static final int BTN_4			= 0x104;
	    public static final int BTN_5			= 0x105;
	    public static final int BTN_6			= 0x106;
	    public static final int BTN_7			= 0x107;
	    public static final int BTN_8			= 0x108;
	    public static final int BTN_9			= 0x109;

	    public static final int BTN_MOUSE		= 0x110;
	    public static final int BTN_LEFT		= 0x110;
	    public static final int BTN_RIGHT		= 0x111;
	    public static final int BTN_MIDDLE		= 0x112;
	    public static final int BTN_SIDE		= 0x113;
	    public static final int BTN_EXTRA		= 0x114;
	    public static final int BTN_FORWARD		= 0x115;
	    public static final int BTN_BACK		= 0x116;
	    public static final int BTN_TASK		= 0x117;

	    public static final int BTN_JOYSTICK	= 0x120;
	    public static final int BTN_TRIGGER		= 0x120;
	    public static final int BTN_THUMB		= 0x121;
	    public static final int BTN_THUMB2		= 0x122;
	    public static final int BTN_TOP			= 0x123;
	    public static final int BTN_TOP2		= 0x124;
	    public static final int BTN_PINKIE		= 0x125;
	    public static final int BTN_BASE		= 0x126;
	    public static final int BTN_BASE2		= 0x127;
	    public static final int BTN_BASE3		= 0x128;
	    public static final int BTN_BASE4		= 0x129;
	    public static final int BTN_BASE5		= 0x12a;
	    public static final int BTN_BASE6		= 0x12b;
	    public static final int BTN_DEAD		= 0x12f;

	    public static final int BTN_GAMEPAD		= 0x130;
	    public static final int BTN_SOUTH		= 0x130;
	    public static final int BTN_A			= BTN_SOUTH;
	    public static final int BTN_EAST		= 0x131;
	    public static final int BTN_B			= BTN_EAST;
	    public static final int BTN_C			= 0x132;
	    public static final int BTN_NORTH		= 0x133;
	    public static final int BTN_X			= BTN_NORTH;
	    public static final int BTN_WEST		= 0x134;
	    public static final int BTN_Y			= BTN_WEST;
	    public static final int BTN_Z			= 0x135;
	    public static final int BTN_TL			= 0x136;
	    public static final int BTN_TR			= 0x137;
	    public static final int BTN_TL2			= 0x138;
	    public static final int BTN_TR2			= 0x139;
	    public static final int BTN_SELECT		= 0x13a;
	    public static final int BTN_START		= 0x13b;
	    public static final int BTN_MODE		= 0x13c;
	    public static final int BTN_THUMBL		= 0x13d;
	    public static final int BTN_THUMBR		= 0x13e;

	    public static final int BTN_DIGI		= 0x140;
	    public static final int BTN_TOOL_PEN	= 0x140;
	    public static final int BTN_TOOL_RUBBER	= 0x141;
	    public static final int BTN_TOOL_BRUSH	= 0x142;
	    public static final int BTN_TOOL_PENCIL	= 0x143;
	    public static final int BTN_TOOL_AIRBRUSH= 0x144;
	    public static final int BTN_TOOL_FINGER	= 0x145;
	    public static final int BTN_TOOL_MOUSE	= 0x146;
	    public static final int BTN_TOOL_LENS	= 0x147;
	    public static final int BTN_TOOL_QUINTTAP= 	0x148;	/* Five fingers on trackpad */
	    public static final int BTN_TOUCH		= 0x14a;
	    public static final int BTN_STYLUS		= 0x14b;
	    public static final int BTN_STYLUS2		= 0x14c;
	    public static final int BTN_TOOL_DOUBLETAP= 0x14d;
	    public static final int BTN_TOOL_TRIPLETAP= 0x14e;
	    public static final int BTN_TOOL_QUADTAP= 0x14f;	/* Four fingers on trackpad */

	    public static final int BTN_WHEEL		= 0x150;
	    public static final int BTN_GEAR_DOWN	= 0x150;
	    public static final int BTN_GEAR_UP		= 0x151;
}
