#include <jni.h>
#include "utils_uInputJNI.h"

JNIEXPORT jboolean JNICALL Java_utils_uInputJNI_setup_1uinput_1device
  (JNIEnv *env, jobject obj){
    jboolean res = JNI_FALSE;
  	if(make_gamepad_MIT() == 1){
	//if(setup_uinput_device() == 1){
  		res = JNI_TRUE;
    }
  	else{
  		res = JNI_FALSE;
    }

    return res;
    // printf("HERE %s\n","JNI FUCKERS");
    // return JNI_FALSE;
  }

JNIEXPORT void JNICALL Java_utils_uInputJNI_trigger_1single_1key_1click
  (JNIEnv *env, jobject obj, jint key_code){
  	send_key_click(key_code);
  }

JNIEXPORT void JNICALL Java_utils_uInputJNI_trigger_1axis_1X_1event
  (JNIEnv *env, jobject obj, jint step){

  	// Move pointer to (0,0) location
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_ABS;
    event.code = ABS_RX; 
    //event.code = ABS_X;
    
    event.value = step; 
    write(uinp_fd, &event, sizeof(event));

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(uinp_fd, &event, sizeof(event));

    //printf("Step on the X: %d\n",step);
  }

JNIEXPORT void JNICALL Java_utils_uInputJNI_trigger_1axis_1Y_1event
  (JNIEnv *env, jobject obj, jint step){

  	
  	// Move pointer to (0,0) location
    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_ABS;
    event.code = ABS_RY;
    //event.code = ABS_Y;

    event.value = step; 
    if(write(uinp_fd, &event, sizeof(event)) <0){
        //printf("Unable to write on the Y axis %s","asd");
    }

     event.type = EV_SYN;
     event.code = SYN_REPORT;
     event.value = 0;
     if(write(uinp_fd, &event, sizeof(event)) <0){
        //printf("Unable to sync the Y axis %s","asd");
    }

     //printf("Step on the Y: %d\n",step);
  }


JNIEXPORT void JNICALL Java_utils_uInputJNI_close_1device
  (JNIEnv *env, jobject obj){

  	 /* Destroy the input device */
    ioctl(uinp_fd, UI_DEV_DESTROY);
    /* Close the UINPUT device */
    close(uinp_fd);
  }


/*
Getter for the possible location where the uInput can be stored.
*/
const char* try_to_find_uinput() {
	  static const char* paths[] = {
	    "/dev/uinput",
	    "/dev/input/uinput",
	    "/dev/misc/uinput"
	  };
	  const int num_paths = 3;
	  int i;

	  for (i = 0; i < num_paths; i++) {
	    if (access(paths[i], F_OK) == 0) {
	      return paths[i];
	    }
	  }
	  return NULL; 

}

/*
  Make the uInput joystick as the example from MIT: https://github.com/jgeumlek/MoltenGamepad
*/
int make_gamepad_MIT(){
  //for some reason SP2 can't recognize the joystick if I use only the ABS_RX, ABS_RY. That is why I have to use it like this
      static int abs[] = {ABS_X, ABS_Y, ABS_RX, ABS_RY};
	//static int abs[] = {ABS_RX, ABS_RY};
  //static int abs[] = {ABS_X, ABS_Y};
      static int key[] = {BTN_SOUTH, BTN_EAST, BTN_NORTH, BTN_WEST, BTN_TL, BTN_TR, KEY_ENTER}; //KEY_ENTER is used because the SP2 HTTP server is not running OK and this will make my server to start the sedning Thread to the client. It won't be shown in the jstest-gtk as a joystic button!
      //from the model above values
        // key[0] = 304
        // key[1] = 305
        // key[2] = 307
        // key[3] = 308
        // key[4] = 314
        // key[5] = 315


	  //static int key[] = {BTN_SOUTH, BTN_EAST, BTN_NORTH, BTN_WEST, BTN_SELECT, BTN_MODE, BTN_START, BTN_TL, BTN_TR, BTN_THUMBL, BTN_THUMBR};

	  
      struct uinput_user_dev uidev;
	    //int fd;
  	  int i;
  	  int mode = O_WRONLY;

      is_existing = 0;

      //try to find and open the current isntance of the device if such exists
      // OPTIONAL! Try to load the device. the if is successful but the loading is not
      // uinp_fd = open("/dev/input/js0", mode | O_NONBLOCK);
      // if(uinp_fd >= 0){
      //   //device was found so use it instead of creating new one
      //   printf("\nuinp_fd is %d!!!\n\n",uinp_fd);
      //   is_existing = 1;

      //   char sysfs_device_name[16];

      //   ioctl(uinp_fd, UI_GET_SYSNAME(sizeof(sysfs_device_name)), sysfs_device_name);
      //   printf("/sys/devices/virtual/input/%s\n", sysfs_device_name);
    
      //   return 1;

      // }

      //there is no created joystick gamepad on this machine so try to create one

     // const char* path = try_to_find_uinput(); 
      // if(path == NULL){
      //    printf("ERROR READING UIPNUT %s\n","asd");
      //    return -1;
      // }


      //try to open the link of it
      uinp_fd = open(try_to_find_uinput(), mode | O_NONBLOCK);
      //uinp_fd = open(path, mode | O_NONBLOCK);
      //uinp_fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
      if (uinp_fd < 0) {
        printf("unable to open uinput %d\n",1);
        return -1;
      }
      
      memset(&uidev, 0, sizeof(uidev));
      snprintf(uidev.name, UINPUT_MAX_NAME_SIZE, "Mine Joystick");
        uidev.id.bustype = BUS_USB;
        uinp.id.vendor  = 0x1;
        uinp.id.product = 0x1;
        uinp.id.version = 1;

      if(ioctl(uinp_fd, UI_SET_EVBIT, EV_ABS) < 0 ){
        printf("unable to write %s\n","UI_SET_EVBIT to EV_ABS");
      }

      for (i = 0; i < sizeof(abs)/sizeof(int); i++) {
        if(ioctl(uinp_fd, UI_SET_ABSBIT, abs[i]) < 0 ){
            printf("unable to write %s\n","UI_SET_ABSBIT to "+abs[i]);
        }
        //example with maximum values
        // uidev.absmin[abs[i]] = -32768;
        // uidev.absmax[abs[i]] = 32767;
        // uidev.absflat[abs[i]] = 1024;
        //set the dimensions of the device
        uidev.absmin[abs[i]] = -1024;
        uidev.absmax[abs[i]] = 1024;
        uidev.absflat[abs[i]] = 0;
      }

      

      if(ioctl(uinp_fd, UI_SET_EVBIT, EV_KEY) < 0 ){
        printf("unable to write %s\n","UI_SET_EVBIT to EV_KEY");
      }

      
      //for (i = 0; key[i] >= 0; i++) {
      for (i = 0; i < sizeof(key)/sizeof(int); i++) {
        if(ioctl(uinp_fd, UI_SET_KEYBIT, key[i]) < 0 ){
            printf("unable to write %s\n","UI_SET_KEYBIT to "+key[i]);
        }
      }


      if(ioctl(uinp_fd, UI_SET_PHYS, "js0") < 0 ){
            printf("unable to write %s\n","UI_SET_PHYS to js0");
      }

      


      write(uinp_fd, &uidev, sizeof(uidev));
      if (ioctl(uinp_fd, UI_DEV_CREATE) < 0)
            printf("uinput device unsuccessful creation %d\n",1);


      return 1; 

}

/* Setup the uinput device. NOT USED!*/
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
        // printf("unable to write");
        printf("unable to write %s\n","asd");

    // if(ioctl(uinp_fd, UI_SET_EVBIT, EV_REL)<0)
    //     printf("unable to write %s\n","asd");

    if(ioctl(uinp_fd, UI_SET_EVBIT, EV_ABS)<0)
        printf("unable to write UI_SET_EVBIT with EV_ABS %s\n","asd");

    // if(ioctl(uinp_fd, UI_SET_RELBIT, REL_X)<0)
    //     printf("unable to write %s\n","asd");
    
    if(ioctl(uinp_fd, UI_SET_ABSBIT, ABS_Y)<0){
        printf("unable to write %s\n","asd");
    }else{
        printf("successfull %s\n","asd");
    }

    for (i=0; i < 256; i++) {
        ioctl(uinp_fd, UI_SET_KEYBIT, i);
    }
    
    ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MOUSE); //this line changes the pointer from keyboard to virtual
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_TOUCH);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MOUSE);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_LEFT);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_MIDDLE);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_RIGHT);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_FORWARD);
    //ioctl(uinp_fd, UI_SET_KEYBIT, BTN_BACK);


    memset(&uinp,0,sizeof(uinp)); // Intialize the uInput device to NULL
    snprintf(uinp.name, UINPUT_MAX_NAME_SIZE, "uinput-sample");
    uinp.id.bustype = BUS_USB;
    uinp.id.vendor  = ID_VENDOR;
    uinp.id.product = ID_PRODUCT;
    uinp.id.version = ID_VERSION;

    // uinp.absmin[ABS_Y] = -1024;
    // uinp.absmax[ABS_Y] = 1024;
    for(int i = 0; i < ABS_MAX; i++){
        uinp.absmin[i] = 0;
        uinp.absmax[i] = 1023;
    }

    /* Create input device into input sub-system */
    if(write(uinp_fd, &uinp, sizeof(uinp))< 0)
        printf("Unable to write UINPUT device.%s","asd");


    if (ioctl(uinp_fd, UI_DEV_CREATE)< 0)
    {
        printf("Unable to create UINPUT device.%s","asd");
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


/* When using uInput for mapping a keypress to a joystick, you will need to make 
   the program wait before releasing the key press. This is not the case if you 
   are trying to make a standard keyboard presses but it is required for the 
   new initialized joystick because without it, it wont work.*/
void *send_key_click_thread(void *key_code){
    int key_code_int = (intptr_t)key_code;
    if(send_keyevent(key_code_int, ACTION_DOWN) == EXIT_FAILURE)
          printf("Failed to performed %s\n","ACTION_DOWN on BTN_SOUTH");
     
      //this is needed in order the UInput device to detect a normal joystick press
      usleep(50000);
      

      if(send_keyevent(key_code_int, ACTION_UP) == EXIT_FAILURE)
          printf("Failed to performed %s\n","ACTION_UP on BTN_SOUTH");

      return NULL;
}


void send_key_click(int key_code){
    //start it on the thread
    if(pthread_create(&thread, NULL, send_key_click_thread, (void *)(intptr_t)key_code)){

        printf("Unable to create the thread %s.\n", " a");
    }
}



/* An example of the most used uInput functions and how it should be used.*/
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
