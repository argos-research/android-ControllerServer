#include <jni.h>
#include "uInputJNI.h"

JNIEXPORT jboolean JNICALL Java_uInputJNI_setup_1uinput_1device
  (JNIEnv *env, jobject obj){
    jboolean res = JNI_FALSE;
  	if(setup_uinput_device() == 1){
  		res = JNI_TRUE;
    }
  	else{
  		res = JNI_FALSE;
    }

    return res;
    // printf("HERE %s\n","JNI FUCKERS");
    // return JNI_FALSE;
  }

JNIEXPORT void JNICALL Java_uInputJNI_trigger_1single_1key_1click
  (JNIEnv *env, jobject obj, jint key_code){
  	send_key_click(key_code);
  }

JNIEXPORT void JNICALL Java_uInputJNI_trigger_1axis_1X_1event
  (JNIEnv *env, jobject obj, jint step){

  	// Move pointer to (0,0) location
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_REL;
    event.code = REL_X;
    //event.code = REL_RX;
    //event.value = -30; //TODO use jint here
    event.value = step; //TODO use jint here

    write(uinp_fd, &event, sizeof(event));
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;

  }

JNIEXPORT void JNICALL Java_uInputJNI_trigger_1axis_1Y_1event
  (JNIEnv *env, jobject obj, jint step){

  	printf("Step %d\n",step);
  	// Move pointer to (0,0) location
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_ABS;
    event.code = ABS_Y;
    //event.code = REL_RY;
    //event.value = 30; //TODO use jint here
    event.value = step; //TODO use jint here

    write(uinp_fd, &event, sizeof(event));
     event.type = EV_SYN;
     event.code = SYN_REPORT;
     event.value = 0;
  }


JNIEXPORT void JNICALL Java_uInputJNI_close_1device
  (JNIEnv *env, jobject obj){

  	 /* Destroy the input device */
    ioctl(uinp_fd, UI_DEV_DESTROY);
    /* Close the UINPUT device */
    close(uinp_fd);
  }



/* Setup the uinput device */
int setup_uinput_device()
{
    // Temporary variable
    int i=0;
    // Open the input device
    uinp_fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
    if (uinp_fd<0)
    {
        printf("Unable to open /dev/uinput\n");
        //die("error: open");
        return -1;
    }

    // Setup the uinput device
    if(ioctl(uinp_fd, UI_SET_EVBIT, EV_KEY)<0)
        printf("unable to write");
    if(ioctl(uinp_fd, UI_SET_EVBIT, EV_REL)<0)
        printf("unable to write");
    if(ioctl(uinp_fd, UI_SET_RELBIT, REL_X)<0)
        printf("unable to write");
    if(ioctl(uinp_fd, UI_SET_ABSBIT, ABS_Y)<0)
        printf("unable to write");
    for (i=0; i < 256; i++) {
        ioctl(uinp_fd, UI_SET_KEYBIT, i);
    }
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MOUSE);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_TOUCH);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MOUSE);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_LEFT);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MIDDLE);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_RIGHT);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_FORWARD);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_BACK);


    memset(&uinp,0,sizeof(uinp)); // Intialize the uInput device to NULL
    snprintf(uinp.name, UINPUT_MAX_NAME_SIZE, "uinput-sample");
    uinp.id.bustype = BUS_VIRTUAL;
    uinp.id.vendor  = 0x1;
    uinp.id.product = 0x1;
    uinp.id.version = 1;

    uinp.absmin[ABS_Y] = -1024;
    uinp.absmax[ABS_Y] = 1024;

    /* Create input device into input sub-system */
    if(write(uinp_fd, &uinp, sizeof(uinp))< 0)
        printf("Unable to write UINPUT device.");


    if (ioctl(uinp_fd, UI_DEV_CREATE)< 0)
    {
        printf("Unable to create UINPUT device.");
        //die("error: ioctl");
        return -1;
    }
    //make it wait because it takes some time to init the whole thing.
    sleep(1);

    return 1;
}

/**
 * send_button
 * key_code
 * is_down
 * Return EXIT_SUCCESS, or EXIT_FAILURE.
 */
int send_keyevent(int key_code, int is_down) {
	int ret;
    // Report BUTTON CLICK - event
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_KEY;
    event.code = key_code;
    event.value = is_down;
    ret = write(uinp_fd, &event, sizeof(event));
    if(ret == -1)
    	goto err;
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;

    ret = write(uinp_fd, &event, sizeof(event));
    if(ret == -1)
    	goto err;

    return EXIT_SUCCESS;
err:
    return EXIT_FAILURE;
}

void send_key_click(int key_code){
	send_keyevent(key_code, ACTION_DOWN);
	send_keyevent(key_code, ACTION_UP);
}

// void send_a_button2()
// {
// 	if(setup_uinput_device() > 0){
// 		//always wait 1 sec for the initialization otherwise it wont work
// 		sleep(1);

// 		printf("sending\n");

// 		send_keyevent(KEY_A, ACTION_DOWN);
// 		send_keyevent(KEY_A, ACTION_UP);

// 		send_click_events();

// 		sleep(2);
// 		/* Destroy the input device */
// 	    ioctl(uinp_fd, UI_DEV_DESTROY);
// 	    /* Close the UINPUT device */
// 	    close(uinp_fd);
// 	}else{
// 		printf("not sending\n");
// 	}

// }



void send_click_events()
{
    // Move pointer to (0,0) location
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_REL;
    event.code = REL_X;
    event.value = 100;
    write(uinp_fd, &event, sizeof(event));
    event.type = EV_REL;
    event.code = REL_Y;
    event.value = 100;
    write(uinp_fd, &event, sizeof(event));
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(uinp_fd, &event, sizeof(event));
    // Report BUTTON CLICK - PRESS event
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_KEY;
    event.code = BTN_LEFT;
    event.value = 1;
    write(uinp_fd, &event, sizeof(event));
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(uinp_fd, &event, sizeof(event));
    // Report BUTTON CLICK - RELEASE event
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_KEY;
    event.code = BTN_LEFT;
    event.value = 0;
    write(uinp_fd, &event, sizeof(event));
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(uinp_fd, &event, sizeof(event));
}
