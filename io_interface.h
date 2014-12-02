#ifndef io_interface_h
#define io_interface_h

typedef enum {IO_EVENT_BEGAN, IO_EVENT_MOVED, IO_EVENT_ENDED} event_type_t;

typedef struct _io_event_t
{
	// Used to be an array of int (Worked well on windows,macosx and iphone).
	// but Android return coordinates as float....
	float position[2];
    int button;

    event_type_t type;
} io_event_t;


//Expect event position in windows active+passive surface coordinates system.
void IO_PushEvent(io_event_t* event);

#endif
