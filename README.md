Hotel Booking Application API
=============================
Objective: Build a RESTful API for a hotel booking application using Java Spring Boot. The application should allow users to view available hotels, rooms, and make bookings.

Entities
========
	Hotel:
		ID, 
		Name, 
		Location, 
		Number of Rooms,
		etc.
	Room:
		ID, 
		Room Number, 
		Type (e.g., Single, Double), 
		Price, 
		Availability, 
		etc.
	Booking:
		ID, 
		Check-in Date, 
		Check-out Date, 
		Room ID, 
		Guest Name, 
		Contact Information, 
		etc.

Functionality
=============
    Hotel Management:
        * List all hotels.View details of a specific hotel.
        * Add a new hotel.
        * Update hotel information.
        * Delete a hotel.
    
    Room Management:
        * List all rooms for a specific hotel.
        * View details of a specific room.
        * Add a new room to a hotel.Update room information.
        * Delete a room.
    
    Booking Management:
        * List all bookings.
        * View details of a specific booking.
        * Make a new booking (validate room availability).
        * Cancel a booking.
    
    Validation:
        * Ensure that rooms are available for booking within the specified date range.
        * Validate input data for creating/modifying hotels, rooms, and bookings.
    
    Security:
        * Implement a basic authentication mechanism.
        * Allow only authenticated users to make bookings or modify hotel/room information.

Additional Considerations
========================
    * Implement proper exception handling for potential errors.
    * Write unit tests to validate the functionality of critical components.
    * Use DTOs for data transfer between the client and server.
    * Include proper documentation for the API.

Bonus Tasks (Optional)
======================
    * Implement pagination for listing entities.
    * Add search functionality for hotels or rooms.
    * Handle edge cases like concurrent bookings, cancellation policies, etc.