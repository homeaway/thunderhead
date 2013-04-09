!function ($, Backbone, _) {

    "use strict"; // jshint ;_;

    var Calendar = function(options) {
        /*
        * @class Calendar
        *
        * @author Phillip Whisenhunt
        * @contructs Calendar object
        *
        * The Calendar creates a custom jquery ui datepicker that uses Backbone.js for storing/retrieving data and
        * for rendering out the calendar. The calendar accepts the following properties as part of the options object:
        *
        * @param {Number | Array} numberOfMonths The number of calendars to render.
        * @param {boolean} fetch A boolean determining if the model should fetch data.
        * @oaram {String} propertyDruid A property druid to fetch data for.
        * @param {String} el A CSS selector to bind the datepickers to.
        * @param {String} url A URL to hit instead of the default endpoint.
        * @param {String} defaultDate A date in the form of 'YYYY-MM-DD' to start the datepicker on.
        * @param {String} locale The locale for the datepicker, this adjusts the start day of the week. All
        *               locales other then en_US and pt_BR start on Monday.
        * @param {Object} clicked An Object whos keys are event statuses and values are functions that accept
        *                a reservations object. These functions are called on click of each type of event.
        * @param {Object} hovered An object whose keys are represent the on and off hover of an event and whose values
        *               are callback functions for their corresponding hover events. These callback functions are passed
        *               the jQuery element that was hovered along with an array of event objects corresponding to the
        *               element hovered.
        * @param {Function} error A function to handle error events from the model. Generally this should
        *               be used control how to display error messages.
        * @param {Object} calendar An array of default calendar day and day ranges.
        * @param {Object} reservations An object of calendar events that contain specific information for a given
        *               calendar day based on a reservation id.
        *
        * @return {Object} Calendar Calendar object constructor

            Example:

            $('#myCalendar').calendar({
                numberOfMonths:     2,
                propertyDruid:       'c584729e-441a-464a-9f24-2e7cc7294568',
                clicked: {
                    reserve: function(element, reservation) {
                        console.log("Reserve status event clicked!");
                        console.log(reservation);
                    },
                    hold: function(element, reservation) {
                        console.log("Hold status event clicked!");
                        console.log(reservation);
                    }
                },
                hovered: {
                    on: function(element, reservations) {
                        console.log("Event hovered on!");
                    },
                    off: function(element, reservations) {
                        console.log("Event hovered off!");
                    }
                }
                error: function(error) {
                    console.log('There was an error!');
                    console.log(error);
                },
                calendar: {
                    "range": {
                        "startDate": '2013-01-02',
                        "endDate": '2013-01-05',
                        "reservationId": "inquiry"
                    },
                    "2013-02-11": {
                        "status": "RESERVE",
                        "reservationId": "24ba2024-f0dd-4c7d-a744-57396d56fd42"
                    },
                    "2013-02-12": {
                        "status": "RESERVE",
                        "reservationId": "24ba2024-f0dd-4c7d-a744-57396d56fd42"
                    },
                    "2013-02-13": {
                        "status": "RESERVE",
                        "reservationId": "24ba2024-f0dd-4c7d-a744-57396d56fd42"
                    }
                },
                reservations: {
                    "24ba2024-f0dd-4c7d-a744-57396d56fd42": {
                        "guestFirstName": "Jane",
                        "guestLastName": "Doe",
                        "checkinDate": "2013-02-11",
                        "checkoutDate": "2013-02-14",
                        "checkinTime": "01:30",
                        "checkoutTime": "21:00",
                        "status": "RESERVE"
                    },
                    "inquiry": {
                        "status": "INQUIRY"
                        "guestFirstName": "John",
                        "guestLastName": "Doe",
                        "checkinDate": "2013-01-02",
                        "checkoutDate": "2013-01-05",
                        "checkinTime": "02:30",
                        "checkoutTime": "6:45",
                    }
                }
            });
        */
    
        this.Model = Backbone.Model.extend(/** @lends CalendarObject.prototype */{
            /**
            * @class CalendarObject
            *
            * @author Phillip Whisenhunt
            * @augments Backbone.Model
            * @requires jQuery
            * @requires Backbone.js
            * @requires Underscore.js
            * @contructs CalendarObject object
            *
            * @param {map} default Default model attributes to pass in.
            * @param {map} options Extra options to pass into the model.
            */
            initialize: function(attributes, options) {
                // Calculate the start dates and append the inquiry dates to the calendar.
                 var defaultDate = this.get('defaultDate'),
                 calendar        = $.extend(true, [], this.get("calendar"));
                 
                _.bindAll(this, 'formatDate', 'getDefaultDates', 'resolveDates', 'setDataBounds', 'reverseFormatDate', 'getReservationsById');

                calendar = $.merge(calendar, this.getDefaultDates());
                this.set('calendar', calendar);

                // If the user didn't pass in a default date then set the date to the current date else create the start and end date ranges from the default date
                defaultDate === '' ? this.setDataBounds(new Date()) : this.setDataBounds(this.reverseFormatDate(defaultDate));
            },

            /**
             * Sets the bounds of data to fetch based on a date.
             * @param {Date} date The date to se the bounds of data for.
             * @return {boolean} bool JSON response from the server.
             */
            setDataBounds: function(date) {
                // Pass by reference! Copy that date!
                var dateCopy = new Date(date.getTime()),
                dateCopy2    = new Date(date.getTime()),
                endDate      = new Date(date.setMonth(date.getMonth() + 12)),
                startDate    = new Date(dateCopy.setMonth(dateCopy.getMonth() - 12));

                this.set({
                    'endDate': this.formatDate(endDate),
                    'startDate': this.formatDate(startDate),
                    'defaultDate': this.formatDate(dateCopy2)
                });
                return false;
            },

            defaults: {
                // Array of calendar days.
                'calendar': [
                ]
            },

            /**
             * Intercepts the returned JSON response and appends the inquiry dates to it.
             * @return {Object} response JSON response from the server.
             */
            parse: function(response) {
                // resolve the calendar date ranges
                response.calendar = this.resolveDates(response.calendar, response.reservations);
                // add the default dates to the calendar
                response.calendar = $.merge(this.getDefaultDates(), response.calendar);
                return response;
            },

            /**
             * Resolves an object of calendar data containing days and day ranges to
             * to an array of purely calendar days without ranges. It also resolves each calendar
             * day to its correct calendar event object based on reservation id and extends each day
             * object to include the calendar event information.
             *
             * @param {Object} calendarData An array of calendar event objects and event ranges.
             * @param {Object} calendarEvents An object of calendar event objects that contain specific information
             * for a given calendar event. It's keys are ids.
             * @return {Array} dates An array of calendar event objects.
             */
            resolveDates: function(calendarData, calendarEvents) {
                var dates = [],
                key, calendarEvent;

                // Resolves a calendar date and a calendar event to a single object
                function resolveDateAndEvent(calendarDate, calendarEvent) {
                    return  {
                        "date": calendarDate.date,
                        "reservationId": calendarDate.reservationId,
                        /* If the user has passed in a duration use that one, else check if the day falls into a
                        checkin or checkout date, else set the duration full. */
                        "duration": (calendarDate.duration !== 'undefined' ? calendarDate.duration: undefined) || (calendarEvent.checkinDate === calendarDate.date ? 'pm': undefined) || (calendarEvent.checkoutDate === calendarDate.date ? 'am': 'full'),
                        "status":  (typeof calendarEvent !== 'undefined' ? (typeof calendarEvent.status !== 'undefined' ? calendarEvent.status.toLowerCase() : ''): ''),
                        "guestFirstName": (typeof calendarEvent !== 'undefined' ? calendarEvent.guestFirstName: undefined),
                        "guestLastName": (typeof calendarEvent !== 'undefined' ? calendarEvent.guestLastName: undefined),
                        "checkinTime": (typeof calendarEvent !== 'undefined' ? calendarEvent.checkinTime: undefined),
                        "checkoutTime": (typeof calendarEvent !== 'undefined' ? calendarEvent.checkoutTime: undefined)
                    };
                }

                for(key in calendarData) {
                    // Grab the specific calendar event associated with the calendar.
                    calendarEvent = (typeof calendarEvents !== 'undefined' ? calendarEvents[calendarData[key].reservationId] : undefined);

                    // If the user has specified date ranges calculate the dates between the range
                    if(typeof calendarData[key].startDate !== 'undefined' && calendarData[key].startDate !== '' && typeof calendarData[key].endDate !== 'undefined' && calendarData[key].endDate !== '') {

                        var textStartDate  = calendarData[key].startDate,
                        textStartDateParts = textStartDate.split('-'),
                        textEndDate        = calendarData[key].endDate,
                        textEndDateParts   = textEndDate.split('-'),
                        // Roll the index of the month back by 1 since indexing begins at 0
                        inquiryStartDate   = new Date(textStartDateParts[0], (parseInt(textStartDateParts[1], 10) - 1), textStartDateParts[2]),
                        // Roll the index of the month back by 1 since indexing begins at 0
                        inquiryEndDate     = new Date(textEndDateParts[0], (parseInt(textEndDateParts[1], 10) -1), textEndDateParts[2]),
                        currentDate        = inquiryStartDate,
                        numberOfDaysBetween, j;

                        // Append the inquiry start date to the calendar as a pm inquiry day
                        dates.push(resolveDateAndEvent({
                            'date': textStartDate,
                            'duration': 'pm',
                            'reservationId': calendarData[key].reservationId
                        }, calendarEvent));

                        // Compute the number of days between the inquiry start and end date
                        numberOfDaysBetween = (inquiryEndDate - inquiryStartDate)/(1000*60*60*24);
                        currentDate = inquiryStartDate;

                        // For each day between the inquiry stay dates add a full inquiry day to the dates
                        for(j = 1; j < numberOfDaysBetween; j++) {
                            currentDate.setDate(currentDate.getDate()+1);

                            dates.push(resolveDateAndEvent({
                                'date': this.formatDate(currentDate),
                                'duration': 'full',
                                'reservationId': calendarData[key].reservationId
                            }, calendarEvent));
                        }

                        // Append the inquiry end date to the dates as an am inquiry day
                        dates.push(resolveDateAndEvent({
                            'date': textEndDate,
                            'duration': 'am',
                            'reservationId': calendarData[key].reservationId
                        }, calendarEvent));
                        /* Remove the calendar event associated with the day range because the checkout day is already
                        added and doesn't need to be added at the bottom. */
                        delete calendarEvents[calendarData[key].reservationId];
                    }
                    // Else just append the default calendar date
                    else {
                        dates.push(resolveDateAndEvent($.extend(true, {'date': key}, calendarData[key]), calendarEvent));
                    }
                }

                /* Loop through all of the calendar events and add a calendar day for each
                check out day since checkout days are not included in the calendar data. */
                for(key in calendarEvents) {
                    dates.push(resolveDateAndEvent({
                        'date': calendarEvents[key].checkoutDate,
                        'reservationId': key
                    },
                    calendarEvents[key]));
                }

                return dates;
            },

            /**
             * Get an array of calendar data containing all days for default dates set.
             * @return {Array} calendar Array of calendar data objects.
             */
            getDefaultDates: function() {
                return this.resolveDates($.extend(true, {}, this.get("defaultCalendar")), $.extend(true, {}, this.get('defaultCalendarEvents')));
            },

            /**
             * Gets all reservations associated with an id.
             * @return {Array} reservations An array of reservation objects.
             */
            getReservationsById: function(id) {
                return _.where($.extend(true, [], this.get("calendar")), { 'reservationId': id });
            },

            /**
             * Takes a date object and returns it in the form of YYYY-MM-DD
             * @param {Date} date Date object to format
             * @return {String} date String representation of a date formatted as YYYY-MM-DD
             */
            formatDate: function(date) {
                var day  = date.getDate().toString(),
                   month = (date.getMonth() + 1).toString();
                return date.getFullYear().toString() + '-' + (month[1] ? month : "0" + month[0]) + '-' + (day[1] ? day : "0" + day[0]);
            },

            /**
             * Takes a string in the form of YYYY-MM-DD and returns a date object
             * @param {String} date String representation of a date formatted as YYYY-MM-DD
             * @return {Date} date Date object to format
             */
            reverseFormatDate: function(date) {
                var dateParts = date.split('-');
                return new Date(parseInt(dateParts[0], 10), (parseInt(dateParts[1], 10) - 1), parseInt(dateParts[2], 10));
            },

            url: function() {
                return this.get('url') + this.get('propertyDruid') + '?startDate=' + this.get('startDate') + '&endDate=' + this.get('endDate');
            }
        });

        // Extend the datepicker to have an aftershow method. This is used to bind the hover events to the calendar.
        if(!$.datepicker._modifiedXYZ) {
            $.datepicker._updateDatepicker_original = $.datepicker._updateDatepicker;
            $.datepicker._updateDatepicker = function(inst) {
                $.datepicker._updateDatepicker_original(inst);
                var afterShow = this._get(inst, 'afterShow');

                if (afterShow) {
                    afterShow.apply((inst.input ? inst.input[0] : null));
                }
            };
            /* This is a custom flag added to the datepicker so that in case a user recreates a datepicker on an object it
            won't exceed the callstack by continually extending the global function. */
            $.datepicker._modifiedXYZ = true;
        }

        this.View = Backbone.View.extend(/** @lends CalendarView.prototype */{
            /*
            * @class CalendarView
            *
            * @author Phillip Whisenhunt
            * @augments Backbone.View
            * @requires jQuery
            * @requires Backbone.js
            * @requires Underscore.js
            * @contructs CalendarView object
            *
            * Model change should ALWAYS be used for rendering the view because it will trigger the correct render function.
            */
            initialize: function(options) {
                var that = this;

                _.bindAll(this, 'fetchData');

                // Extend each function passed in to options.clicked to trigger a jQuery custom event
                var extendOptionsFunction = function(key) {

                    var cachedFunction = options.clicked[key];
                    options.clicked[key] = function(element, reservation) {
                        // Trigger a jQuery custom event
                        $(that.el).trigger(key, [element, reservation]);
                        cachedFunction.call(this, element, reservation);
                    };
                };

                for(var key in options.clicked) {
                    extendOptionsFunction(key);
                }

                // Extend the properties of the view with the click/hovered/error functions, hover classes, and end calendar
                $.extend(true, this, { 'clicked': options.clicked });
                $.extend(true, this, { 'hovered': options.hovered });
                $.extend(true, this, { "error": options.error });
                this.endCalendar = options.endCalendar;
                this.hoverClass = options.hoverClass;
                this.hoverClassStart = options.hoverClassStart;
                this.hoverClassEnd = options.hoverClassEnd;
                this.fetching = options.fetch;
                this.currentElement = undefined;

                // In case there is not Backbone Layout manager, bind to after render.
                if(typeof Backbone.Layout === 'undefined') {
                    _.bindAll(this, 'afterRender');
                    this.model.on('change', this.afterRender, this);
                }
                else {
                    this.model.on('change', this.render, this);
                }

                this.model.on('error', function(error) {
                    $(that.el).trigger('error', error);
                    that.error(error);
                });

                // If the start date has changed then fetch data!
                this.model.on('change:startDate', function() {
                    that.fetchData(options.fetch);
                });

                this.fetchData(options.fetch);
            },


            /**
             * Fetchs data from the endpoint.
             * @param {boolean} fetching Determines wether to fetch data or not.
             * @param {boolean} bool Returns false
             */
            fetchData: function(fetching) {
                var that = this;

                // Switch fetch to on so that the loader is displayed
                this.fetching = fetching;
                // Trigger a change which will rerender the datepicker.
                this.model.trigger('change');

                if(this.model.get('propertyDruid') !== '' && this.fetching){

                    this.model.fetch({
                        success: function() {
                            that.fetching = false;
                            that.model.trigger('change');
                        },
                        error: function() {
                            that.fetching = false;
                            that.model.trigger('change');
                        }
                    });
                }
                return false;
            },

            afterRender: function() {
                var calendar =  $.extend(true, [], this.model.get("calendar")),
                        that = this,
                      styles = $.extend(true, {}, this.model.get("styles")),
                 defaultDate = this.model.get('defaultDate'),
                      locale = this.model.get('locale'),
                 startOfWeek = 1,
                        // The ID of the currently hovered event.
                        currentId, 
                        // The selected date for the first calendar.
                        selectedStartDate,
                        // The selected date for the second calendar.
                        selectedEndDate, 
                        // The currently hovered end date of the second calendar.
                        hoveredEndDate,
                        // The currently hovered end date of the first calendar.
                        hoveredStartDate;

                /* Everyone except the US and BR starts their week on monday, check the locale to
                adjust the start of the week. */
                if(locale !== 'en_US' || locale !== 'pt_BR') {
                    startOfWeek = 0;
                }

                // Convert the string default date YYYY-MM-DD to a date object for the date picker
                defaultDate = this.model.reverseFormatDate(defaultDate);

                $(this.el).datepicker('destroy').datepicker({

                    defaultDate: defaultDate,

                    firstDay: startOfWeek,

                    numberOfMonths: this.model.get("numberOfMonths"),

                    /**
                     * Before each date is shown on the screen apply the correct styling.
                     * @param {Date} date object of each date being displayed
                     */
                    beforeShowDay: function(date) {
                        var style = '',
                        formattedDate, i;

                        // Convert the date to the format returned by the backend YYYY-MM-DD
                        date = that.model.formatDate(date);

                        /* Loop through all of the calendar days and calculate the style. Start calculating style
                        from the end of the calendar. This is done so that the first class, which represents the
                        id of the reservation, is added first. */
                        for(i = calendar.length - 1; i > -1; i--) {

                            if(calendar[i].date === date) {
                                    style += ' ' + calendar[i].duration + '-' + calendar[i].status.toLowerCase() + ' X' + calendar[i].reservationId + 'X';
                            }
                        }

                        if(typeof that.endCalendar !== 'undefined') {
                            formattedDate = that.model.reverseFormatDate(date);
                            
                            // Color the current date if ...
                            // If the current date is less then the selected second calendar date and the current date is greater then the selected start date
                            if(formattedDate < selectedEndDate && formattedDate > selectedStartDate
                                // Or, if the current date is less then the selected start date and the current date is greater then the start hover date
                                || formattedDate < selectedStartDate && formattedDate > hoveredStartDate
                                // Or, if the current date is equal to the selected start and end date
                                || typeof selectedStartDate !== 'undefined' && typeof selectedEndDate !== 'undefined' && selectedStartDate.toString() === formattedDate.toString()
                                    && selectedEndDate.toString() === selectedStartDate.toString()
                                // Or, if the current date is the selected start date and the start hover date is before the selected date
                                || typeof selectedStartDate !== 'undefined' && typeof hoveredStartDate !== 'undefined'
                                    && hoveredStartDate < selectedStartDate
                                    && selectedStartDate.toString() === formattedDate.toString()) {
                                style += that.hoverClass;
                            }
                            // Or, if the current date is equal to the selected start date
                            else if(typeof selectedStartDate !== 'undefined' && selectedStartDate.toString() === formattedDate.toString()
                                // Or, if the current date is equal to the hoevered start date
                                || typeof hoveredStartDate !== 'undefined' && hoveredStartDate.toString() === formattedDate.toString()) {
                                style += that.hoverClassStart;
                            }
                            // Or, if the current date is the equal to the selected end date
                            else if(typeof selectedEndDate !== 'undefined' && selectedEndDate.toString() === formattedDate.toString()){
                                style += that.hoverClassEnd;
                            }
                        }

                        // Return the style and a blank tooltip.
                        return [true, style, ''];
                    },

                    /**
                     * Whenever the calendar is navigated, calculate if the user is within the bounds of data
                     * that has been fetched. If they are, then set new bounds, which triggers a fetch.
                     * @param {Number} year The year that is currently navigated to
                     * @param {Number} month The month that is currently navigated to
                     * @param {Element} element The date picker instance
                     * @param {boolean} bool Returns false
                     */
                    onChangeMonthYear: function(year, month, element) {
                        if(this.fetching) {
                            // Calculate the start date from it's textual representation of YYYY-MM-DD
                            var startDate        = that.model.get('startDate'),
                            startDateParts       = startDate.split('-'),
                            startDateDate        = new Date(parseInt(startDateParts[0], 10), (parseInt(startDateParts[1], 10) - 1), parseInt(startDateParts[2], 10)),
                            // Calculate the end date from it's textual representation of YYYY-MM-DD
                            endDate              = that.model.get('endDate'),
                            endDateParts         = endDate.split('-'),
                            endDateDate          = new Date(parseInt(endDateParts[0], 10), (parseInt(endDateParts[1], 10) - 1), parseInt(endDateParts[2], 10)),
                            // The number of months in the model
                            numberOfMonths       = that.model.get('numberOfMonths'),
                            // The computed number of months from the model, in case the number of months is an array this stores the true number of months
                            actualNumberOfMonths = numberOfMonths,
                            // The number of months to the left and right boundaries of the data that was fetched
                            monthsToLeft         = 0,
                            monthsToRight        = 0,
                            // The currently displayed left and right months of the calendar
                            farLeftMonth         = new Date(year, (month - 1), 1),
                            farRightMonth, i, startingDate;

                            // If the number of months is an array then calculate the number of months from the array.
                            if(Object.prototype.toString.call(numberOfMonths) === '[object Array]') {

                                actualNumberOfMonths = 0;
                                for(i = 0; i < numberOfMonths.length; i++) {
                                    actualNumberOfMonths += numberOfMonths[i];
                                }
                            }
                            // Calculate the number of months from the start of data fetched to the far left most calendar displayed
                            monthsToLeft = (farLeftMonth.getFullYear() - startDateDate.getFullYear()) * 12;
                            monthsToLeft -= startDateDate.getMonth() + 1;
                            monthsToLeft += farLeftMonth.getMonth();

                            farRightMonth = new Date(farLeftMonth.setMonth(farLeftMonth.getMonth() + actualNumberOfMonths - 2));
                            // Calculate the number of months from the end of data fetched to the far right most calendar displayed
                            monthsToRight = (endDateDate.getFullYear() - farRightMonth.getFullYear()) * 12;
                            monthsToRight -= farRightMonth.getMonth() + 1;
                            monthsToRight += endDateDate.getMonth();

                            // If we are within 1 month of data then set new boundaries, which will trigger a fetch!
                            if(monthsToRight < 1 || monthsToLeft < 1) {
                                that.model.setDataBounds(new Date(year, (month - 1), 1));
                            }
                        }
                        return false;
                    },

                    afterShow: function() {

                        $(".ui-state-default").hover(

                            // On mouse hover
                            function(e) {
                                /* Parent class is in the form of:

                                    duration-status XidX duration-status XidX

                                    During the construction of the class, the am element will always be first and the pm item
                                    will always be last. Splitting along the X delimiter yields [1] as the first id and [3] as the second id. The [0] and [2] positions contain the statuses for the ids.
                                */
                                var parentClass = $(this).parent().attr('class'),
                               parentClassParts = parentClass.split("X"),
                                             id, status, onClickAttributeParts, newHoveredDate;

                                // If the cell contains an ID
                                if(parentClass.indexOf('X') !== -1) {

                                    // set the id to the first id present
                                    id = parentClassParts[1];

                                    /* If there are more then 2 different IDs for a given day then determine which
                                        part of the day is being hovered. Which part of the day is determined by
                                        calculating the relative X and relative Y of the mouse to the cell being hovered.
                                        If the relative X and Y are less then half of the width of the table cell
                                        then the user is hovering the am or top event. */
                                    if((parentClassParts.length - 1) > 3) {

                                        $(this).unbind('mousemove').mousemove(function(e) {
                                            var parentOffset = $(this).parent().offset(),
                                            relativeX = e.pageX - parentOffset.left,
                                            relativeY = e.pageY - parentOffset.top,
                                            // Calculate the diagnol as half of the width of the table cell
                                            diaganol = $(this).parent().width() / 2;

                                            // Remove the hover style for all days tied to either of the 2 ids.
                                            swtichClassBasedOnHover(id, false, status);
                                            swtichClassBasedOnHover(parentClassParts[3], false, status);

                                            // If the user is above the cell on the am event, set the id and determine the hover status.
                                            if(relativeX < diaganol && relativeY < diaganol) {
                                                status = determineStatus(parentClassParts[0]);
                                                id = parentClassParts[1];

                                                // If the user is hovering on the inquiry status, then switch to the other event!
                                                if(status === 'inquiry') {
                                                    status = parentClassParts[2];
                                                    id = parentClassParts[3];
                                                }
                                            }
                                            else {
                                                // Else the user is below the cell on the pm event, set the id and determine the hover status.
                                                status = determineStatus(parentClassParts[2]);
                                                id = parentClassParts[3];

                                                // If the user is hovering on the inquiry status, then switch to the other event!
                                                if(status === 'inquiry') {
                                                    status = parentClassParts[0];
                                                    id = parentClassParts[1];
                                                }
                                            }
                                            // Only trigger a custom hover if the user switches to a new event.
                                            if(currentId !== id) {
                                                that.currentElement = $(this);
                                                that.hovered.on($(this), that.model.getReservationsById(id));
                                            }
                                            currentId = id;
                                            status = determineStatus(status);
                                            swtichClassBasedOnHover(id, true, status);
                                        });
                                    }
                                    /* Else if the user is hovering a cell with a single event. */
                                    else {
                                        currentId = id;
                                        status = determineStatus(parentClass);
                                        swtichClassBasedOnHover(id, true, status);
                                    }
                                    that.currentElement = $(this);
                                    // Call the custom hover event if it exists.
                                    that.hovered.on($(this), that.model.getReservationsById(id));
                                }

                                // Parse out the day that is being hovered, set the currently hovered date, and refresh the datepicker
                                //DP_jQuery.datepicker._selectDay('#dp1361889444024',1,2013, this);return false;
                                onClickAttributeParts = $(this).parent().attr('onclick').split(',');
                                newHoveredDate = that.model.reverseFormatDate(onClickAttributeParts[2] + '-' + (parseInt(onClickAttributeParts[1], 10) + 1) + '-' + $(this).html());

                                // If the hoveredStartDate is different then the currently selected date then set it
                                if(typeof hoveredStartDate === 'undefined' || hoveredStartDate.toString() !== newHoveredDate.toString()) {
                                    hoveredStartDate = newHoveredDate;
                                    $(that.el).datepicker('refresh');
                                }
                            },
                            // On the user leaving mouse hover
                            function(e) {
                                /* Parent class is in the form of:

                                    duration-status XidX duration-status XidX

                                    During the construction of the class, the am element will always be first and the pm item
                                    will always be last. Splitting along the X delimiter yields [1] as the first id and [3] as
                                    the second id.
                                */
                                var parentClass = $(this).parent().attr('class'),
                               parentClassParts = parentClass.split("X"),
                                             id = parentClassParts[1];

                                // If there are 2 ids present then remove all hover events for the second id.
                                if((parentClassParts.length - 1) > 3) {
                                    swtichClassBasedOnHover(parentClassParts[3], false, '');
                                    that.currentElement = $(this);
                                    that.hovered.off($(this), that.model.getReservationsById(id));
                                }
                                if(typeof id !== 'undefined') {
                                    swtichClassBasedOnHover(id, false, '');
                                    that.currentElement = $(this);
                                    that.hovered.off($(this), that.model.getReservationsById(id));
                                }
                            }
                        );

                        /* Determines the status(hold, reserve, unavailable, etc.) for a given string.
                         * @param {String} classAttribute String representing the classes of an element.
                         */
                        function determineStatus(classAttribute) {

                            if(classAttribute.indexOf('reserve') !== -1){
                                return 'reserve';
                            }
                            else if(classAttribute.indexOf('hold') !== -1){
                                return 'hold';
                            }
                            else if(classAttribute.indexOf('delete') !== -1){
                                return 'delete';
                            }
                            else if(classAttribute.indexOf('cancel') !== -1){
                                return 'cancel';
                            }
                            else if(classAttribute.indexOf('unavailable') !== -1){
                                return 'unavailable';
                            }
                            else if(classAttribute.indexOf('inquiry') !== -1){
                                return 'inquiry';
                            }
                            else {
                                return '';
                            }
                        }

                        /* Switches the calendars hover state for a given event based on id.
                         * @param {String} id String representing the classes of an element.
                         * @param {boolean} isHovered Boolean determining if the hover state should be added or removed.
                         * @param {String} status String of the status(hold, reserve, unavailable, etc.) to add to an element.
                         */
                        function swtichClassBasedOnHover(id, isHovered, status) {
                            // If we are on an inquiry, don't do anything!
                            if(status !== 'inquiry') {

                                var cell, cells, cellClass, cellClassParts, i;

                                // grab all of the cells with the given delimited id
                                cells = $(that.el).find('.X' + id + 'X');

                                if(isHovered) {
                                    // Loop through all of the cells and add the correct status hover class.
                                    for(i = 0; i < cells.length; i++) {
                                        cell = $(cells[i]);

                                        cellClassParts = cell.attr('class').split('X');

                                        /* Determine which part of the class corresponds to the ID being hovered.
                                            This is put place so that in case there are two events of the same type,
                                            only the event corresponding to the ID will have its class changed. */
                                        if(cellClassParts[1] === id){
                                            cellClassParts[0] = cellClassParts[0].replace('full-' + status, 'full-' + status + '-hover');
                                            cellClassParts[0] = cellClassParts[0].replace('am-' + status, 'am-' + status + '-hover');
                                            cellClassParts[0] = cellClassParts[0].replace('pm-' + status, 'pm-' + status + '-hover');
                                        }
                                        else {
                                            cellClassParts[2] = cellClassParts[2].replace('full-' + status, 'full-' + status + '-hover');
                                            cellClassParts[2] = cellClassParts[2].replace('am-' + status, 'am-' + status + '-hover');
                                            cellClassParts[2] = cellClassParts[2].replace('pm-' + status, 'pm-' + status + '-hover');
                                        }
                                        cell.attr('class', cellClassParts.join('X'));
                                    }
                                }
                                else {
                                    // Loop through all of the cells and remove any hover classes.
                                    for(i = 0; i < cells.length; i++) {
                                        // Unbind the mousemove event from each cell, helps memory cleanup :)
                                        cell = $(cells[i]).unbind('mousemove');
                                        cellClass = cell.attr('class');

                                        cellClass = cellClass.replace(/-hover/g, '');

                                        cell.attr('class', cellClass);
                                    }
                                }
                            }
                        }
                    },

                    /* Fires when a specific date is clicked on the calendar.
                     * @param {String} dateText String representation of the date clicked on.
                     */
                    onSelect: function(dateText, element) {
                        var dateTextParts = dateText.split('/'),
                                     date = dateTextParts[2] + '-' + dateTextParts[0] + '-' + dateTextParts[1],
                                     i, newSelectedStartDate;

                        // STOP THE DATEPICKER FROM REDRAWING ON CLICK! OMG!
                        element.inline = false;

                        // Find the correct date that we clicked on and trigger the corresponding status function.
                        for(i = 0; i < calendar.length; i++) {

                            if(calendar[i].date === date && calendar[i].reservationId === currentId) {

                                // Check if there is a function for that status
                                if(typeof that.clicked[calendar[i].status.toLowerCase()] !== 'undefined'){
                                    that.clicked[calendar[i].status.toLowerCase()](that.currentElement, calendar[i]);
                                }
                                // Trigger a jQuery event for the status clicked if there isn't a function for the status
                                else {
                                    $(that.el).trigger(calendar[i].status.toLowerCase(), that.currentElement, calendar[i]);
                                }
                            }
                        }

                        newSelectedStartDate = that.model.reverseFormatDate(date);

                        // If the selected start date is new or different, broadcast a change event
                        if(typeof selectedStartDate === 'undefined' || newSelectedStartDate.toString() !== selectedStartDate.toString()) {
                            $(that.el).trigger('changed');
                        }
                        selectedStartDate = newSelectedStartDate;
                    },

                    /* Fires when the datepicker is closed.
                     * @param {String} dateText String representation of the date clicked on.
                     * @param {DatePicker} element The datepicker instance.
                     */
                    onClose: function(dateText, element) {
                        // If there are two calendars present
                        if (typeof that.endCalendar !== 'undefined') {
                            // Set the hover start date to undefined so that the hover dates won't be painted in the redraw
                            hoveredStartDate = undefined;
                            // When the first date picker closes refresh it and unfocus the datepicker
                            $(that.el).datepicker('refresh').trigger('blur');

                            // If the user selects a start date that is greater then the second date then set the second date equal to the start date
                            if(selectedEndDate < selectedStartDate || typeof selectedEndDate === 'undefined'){
                                selectedEndDate = selectedStartDate;
                                $(that.endCalendar).datepicker('setDate', selectedEndDate);
                                $(that.endCalendar).trigger('changed');
                            }                        
                        }
                    }
                });

                // If there is a second calendar then create it
                if(typeof this.endCalendar !== 'undefined') {

                    $(this.endCalendar).datepicker('destroy').datepicker({

                        defaultDate: defaultDate,

                        firstDay: startOfWeek,

                        numberOfMonths: this.model.get("numberOfMonths"),

                        /**
                         * Before each date is shown on the screen apply the correct styling.
                         * @param {Date} date object of each date being displayed
                         */
                        beforeShowDay: function(date) {
                            var style = '',
                            formattedDate, i;

                            // Convert the date to the format returned by the backend YYYY-MM-DD
                            date = that.model.formatDate(date);

                            /* Loop through all of the calendar days and calculate the style. Start calculating style
                            from the end of the calendar. This is done so that the first class, which represents the
                            id of the reservation, is added first. */
                            for(i = calendar.length - 1; i > -1; i--) {

                                if(calendar[i].date === date) {
                                        style += ' ' + calendar[i].duration + '-' + calendar[i].status.toLowerCase() + ' X' + calendar[i].reservationId + 'X';
                                }
                            }

                            formattedDate = that.model.reverseFormatDate(date);

                            // Color the current date if ...
                            // If the curent date is between the selected first date and end hover date and the selected first date is less then end hover date
                            if(selectedStartDate < hoveredEndDate && selectedStartDate < formattedDate && formattedDate < hoveredEndDate 
                                // Or, if the current date is greater then the selected date and less then the selected end date
                                || typeof selectedEndDate !== 'undefined' && formattedDate > selectedStartDate && formattedDate < selectedEndDate
                                // Or, if the current date is equal to the selected start and end date
                                || typeof selectedStartDate !== 'undefined' && typeof selectedEndDate !== 'undefined' 
                                    && selectedStartDate.toString() === formattedDate.toString()
                                    && selectedEndDate.toString() === selectedStartDate.toString()
                                ) {
                                style += that.hoverClass;
                            }
                            // Or, if the current date is equal to the selected start date
                            else if(typeof selectedStartDate !== 'undefined' && selectedStartDate.toString() === formattedDate.toString()) {
                                style += that.hoverClassStart;
                            }
                            // Or, if the current date is equal to the selected end date
                            else if(typeof selectedEndDate !== 'undefined' && formattedDate.toString() === selectedEndDate.toString()
                                // Or, if the current date is equal to the end hover date
                                || typeof hoveredEndDate !== 'undefined' && formattedDate.toString() === hoveredEndDate.toString()) {
                                style += that.hoverClassEnd;
                            }

                            // Return the style and a blank tooltip.
                            return [true, style, ''];
                        },

                        /**
                         * Whenever the calendar is navigated, calculate if the user is within the bounds of data
                         * that has been fetched. If they are, then set new bounds, which triggers a fetch.
                         * @param {Number} year The year that is currently navigated to
                         * @param {Number} month The month that is currently navigated to
                         * @param {Element} element The date picker instance
                         * @param {boolean} bool Returns false
                         */
                        onChangeMonthYear: function(year, month, element) {
                            if(this.fetching) {
                                // Calculate the start date from it's textual representation of YYYY-MM-DD
                                var startDate        = that.model.get('startDate'),
                                startDateParts       = startDate.split('-'),
                                startDateDate        = new Date(parseInt(startDateParts[0], 10), (parseInt(startDateParts[1], 10) - 1), parseInt(startDateParts[2], 10)),
                                // Calculate the end date from it's textual representation of YYYY-MM-DD
                                endDate              = that.model.get('endDate'),
                                endDateParts         = endDate.split('-'),
                                endDateDate          = new Date(parseInt(endDateParts[0], 10), (parseInt(endDateParts[1], 10) - 1), parseInt(endDateParts[2], 10)),
                                // The number of months in the model
                                numberOfMonths       = that.model.get('numberOfMonths'),
                                // The computed number of months from the model, in case the number of months is an array this stores the true number of months
                                actualNumberOfMonths = numberOfMonths,
                                // The number of months to the left and right boundaries of the data that was fetched
                                monthsToLeft         = 0,
                                monthsToRight        = 0,
                                // The currently displayed left and right months of the calendar
                                farLeftMonth         = new Date(year, (month - 1), 1),
                                farRightMonth, i, startingDate;

                                // If the number of months is an array then calculate the number of months from the array.
                                if(Object.prototype.toString.call(numberOfMonths) === '[object Array]') {

                                    actualNumberOfMonths = 0;
                                    for(i = 0; i < numberOfMonths.length; i++) {
                                        actualNumberOfMonths += numberOfMonths[i];
                                    }
                                }
                                // Calculate the number of months from the start of data fetched to the far left most calendar displayed
                                monthsToLeft = (farLeftMonth.getFullYear() - startDateDate.getFullYear()) * 12;
                                monthsToLeft -= startDateDate.getMonth() + 1;
                                monthsToLeft += farLeftMonth.getMonth();

                                farRightMonth = new Date(farLeftMonth.setMonth(farLeftMonth.getMonth() + actualNumberOfMonths - 2));
                                // Calculate the number of months from the end of data fetched to the far right most calendar displayed
                                monthsToRight = (endDateDate.getFullYear() - farRightMonth.getFullYear()) * 12;
                                monthsToRight -= farRightMonth.getMonth() + 1;
                                monthsToRight += endDateDate.getMonth();

                                // If we are within 1 month of data then set new boundaries, which will trigger a fetch!
                                if(monthsToRight < 1 || monthsToLeft < 1) {
                                    that.model.setDataBounds(new Date(year, (month - 1), 1));
                                }
                            }
                            return false;
                        },

                        afterShow: function() {

                            $(".ui-state-default").hover(

                                // On mouse hover
                                function(e) {
                                    /* Parent class is in the form of:

                                        duration-status XidX duration-status XidX

                                        During the construction of the class, the am element will always be first and the pm item
                                        will always be last. Splitting along the X delimiter yields [1] as the first id and [3] as the second id. The [0] and [2] positions contain the statuses for the ids.
                                    */
                                    var parentClass = $(this).parent().attr('class'),
                                   parentClassParts = parentClass.split("X"),
                                                 id, status, onClickAttributeParts, newHoveredDate;

                                    // If the cell contains an ID
                                    if(parentClass.indexOf('X') !== -1) {

                                        // set the id to the first id present
                                        id = parentClassParts[1];

                                        /* If there are more then 2 different IDs for a given day then determine which
                                            part of the day is being hovered. Which part of the day is determined by
                                            calculating the relative X and relative Y of the mouse to the cell being hovered.
                                            If the relative X and Y are less then half of the width of the table cell
                                            then the user is hovering the am or top event. */
                                        if((parentClassParts.length - 1) > 3) {

                                            $(this).unbind('mousemove').mousemove(function(e) {
                                                var parentOffset = $(this).parent().offset(),
                                                relativeX = e.pageX - parentOffset.left,
                                                relativeY = e.pageY - parentOffset.top,
                                                // Calculate the diagnol as half of the width of the table cell
                                                diaganol = $(this).parent().width() / 2;

                                                // Remove the hover style for all days tied to either of the 2 ids.
                                                swtichClassBasedOnHover(id, false, status);
                                                swtichClassBasedOnHover(parentClassParts[3], false, status);

                                                // If the user is above the cell on the am event, set the id and determine the hover status.
                                                if(relativeX < diaganol && relativeY < diaganol) {
                                                    status = determineStatus(parentClassParts[0]);
                                                    id = parentClassParts[1];

                                                    // If the user is hovering on the inquiry status, then switch to the other event!
                                                    if(status === 'inquiry') {
                                                        status = parentClassParts[2];
                                                        id = parentClassParts[3];
                                                    }
                                                }
                                                else {
                                                    // Else the user is below the cell on the pm event, set the id and determine the hover status.
                                                    status = determineStatus(parentClassParts[2]);
                                                    id = parentClassParts[3];

                                                    // If the user is hovering on the inquiry status, then switch to the other event!
                                                    if(status === 'inquiry') {
                                                        status = parentClassParts[0];
                                                        id = parentClassParts[1];
                                                    }
                                                }
                                                // Only trigger a custom hover if the user switches to a new event.
                                                if(currentId !== id) {
                                                    that.currentElement = $(this);
                                                    that.hovered.on($(this), that.model.getReservationsById(id));
                                                }
                                                currentId = id;
                                                status = determineStatus(status);
                                                swtichClassBasedOnHover(id, true, status);
                                            });
                                        }
                                        /* Else if the user is hovering a cell with a single event. */
                                        else {
                                            currentId = id;
                                            status = determineStatus(parentClass);
                                            swtichClassBasedOnHover(id, true, status);
                                        }
                                        that.currentElement = $(this);
                                        // Call the custom hover event if it exists.
                                        that.hovered.on($(this), that.model.getReservationsById(id));
                                    }

                                    // Parse out the day that is being hovered, set the currently hovered date, and refresh the datepicker
                                    //DP_jQuery.datepicker._selectDay('#dp1361889444024',1,2013, this);return false;
                                    onClickAttributeParts = $(this).parent().attr('onclick').split(',');
                                    newHoveredDate = that.model.reverseFormatDate(onClickAttributeParts[2] + '-' + (parseInt(onClickAttributeParts[1], 10) + 1) + '-' + $(this).html());

                                    // If the hoveredEndDate is different then the currently selected date then set it
                                    if(typeof hoveredEndDate === "undefined" || hoveredEndDate.toString() !== newHoveredDate.toString()) {
                                        hoveredEndDate = newHoveredDate;
                                        $(that.endCalendar).datepicker('refresh');
                                    }
                                },
                                // On the user leaving mouse hover
                                function(e) {
                                    /* Parent class is in the form of:

                                        duration-status XidX duration-status XidX

                                        During the construction of the class, the am element will always be first and the pm item
                                        will always be last. Splitting along the X delimiter yields [1] as the first id and [3] as
                                        the second id.
                                    */
                                    var parentClass = $(this).parent().attr('class'),
                                   parentClassParts = parentClass.split("X"),
                                                 id = parentClassParts[1];

                                    // If there are 2 ids present then remove all hover events for the second id.
                                    if((parentClassParts.length - 1) > 3) {
                                        swtichClassBasedOnHover(parentClassParts[3], false, '');
                                        that.currentElement = $(this);
                                        that.hovered.off($(this), that.model.getReservationsById(id));
                                    }
                                    if(typeof id !== 'undefined') {
                                        swtichClassBasedOnHover(id, false, '');
                                        that.currentElement = $(this);
                                        that.hovered.off($(this), that.model.getReservationsById(id));
                                    }
                                }
                            );

                            /* Determines the status(hold, reserve, unavailable, etc.) for a given string.
                             * @param {String} classAttribute String representing the classes of an element.
                             */
                            function determineStatus(classAttribute) {

                                if(classAttribute.indexOf('reserve') !== -1){
                                    return 'reserve';
                                }
                                else if(classAttribute.indexOf('hold') !== -1){
                                    return 'hold';
                                }
                                else if(classAttribute.indexOf('delete') !== -1){
                                    return 'delete';
                                }
                                else if(classAttribute.indexOf('cancel') !== -1){
                                    return 'cancel';
                                }
                                else if(classAttribute.indexOf('unavailable') !== -1){
                                    return 'unavailable';
                                }
                                else if(classAttribute.indexOf('inquiry') !== -1){
                                    return 'inquiry';
                                }
                                else {
                                    return '';
                                }
                            }

                            /* Switches the calendars hover state for a given event based on id.
                             * @param {String} id String representing the classes of an element.
                             * @param {boolean} isHovered Boolean determining if the hover state should be added or removed.
                             * @param {String} status String of the status(hold, reserve, unavailable, etc.) to add to an element.
                             */
                            function swtichClassBasedOnHover(id, isHovered, status) {
                                // If we are on an inquiry, don't do anything!
                                if(status !== 'inquiry') {

                                    var cell, cells, cellClass, cellClassParts, i;

                                    // grab all of the cells with the given delimited id
                                    cells = $(that.el).find('.X' + id + 'X');

                                    if(isHovered) {
                                        // Loop through all of the cells and add the correct status hover class.
                                        for(i = 0; i < cells.length; i++) {
                                            cell = $(cells[i]);

                                            cellClassParts = cell.attr('class').split('X');

                                            /* Determine which part of the class corresponds to the ID being hovered.
                                                This is put place so that in case there are two events of the same type,
                                                only the event corresponding to the ID will have its class changed. */
                                            if(cellClassParts[1] === id){
                                                cellClassParts[0] = cellClassParts[0].replace('full-' + status, 'full-' + status + '-hover');
                                                cellClassParts[0] = cellClassParts[0].replace('am-' + status, 'am-' + status + '-hover');
                                                cellClassParts[0] = cellClassParts[0].replace('pm-' + status, 'pm-' + status + '-hover');
                                            }
                                            else {
                                                cellClassParts[2] = cellClassParts[2].replace('full-' + status, 'full-' + status + '-hover');
                                                cellClassParts[2] = cellClassParts[2].replace('am-' + status, 'am-' + status + '-hover');
                                                cellClassParts[2] = cellClassParts[2].replace('pm-' + status, 'pm-' + status + '-hover');
                                            }
                                            cell.attr('class', cellClassParts.join('X'));
                                        }
                                    }
                                    else {
                                        // Loop through all of the cells and remove any hover classes.
                                        for(i = 0; i < cells.length; i++) {
                                            // Unbind the mousemove event from each cell, helps memory cleanup :)
                                            cell = $(cells[i]).unbind('mousemove');
                                            cellClass = cell.attr('class');

                                            cellClass = cellClass.replace(/-hover/g, '');

                                            cell.attr('class', cellClass);
                                        }
                                    }
                                }
                            }
                        },

                        /* Fires when a specific date is clicked on the calendar.
                         * @param {String} dateText String representation of the date clicked on.
                         * @param {DatePicker} element The datepicker instance.
                         */
                        onSelect: function(dateText, element) {
                            var dateTextParts = dateText.split('/'),
                                         date = dateTextParts[2] + '-' + dateTextParts[0] + '-' + dateTextParts[1],
                                         i, newSelectedEndDate;

                            // STOP THE DATEPICKER FROM REDRAWING ON CLICK! OMG!
                            element.inline = false;

                            // Find the correct date that we clicked on and trigger the corresponding status function.
                            for(i = 0; i < calendar.length; i++) {

                                if(calendar[i].date === date && calendar[i].reservationId === currentId) {

                                    // Check if there is a function for that status
                                    if(typeof that.clicked[calendar[i].status.toLowerCase()] !== 'undefined'){
                                        that.clicked[calendar[i].status.toLowerCase()](that.currentElement, calendar[i]);
                                    }
                                    // Trigger a jQuery event for the status clicked if there isn't a function for the status
                                    else {
                                        $(that.el).trigger(calendar[i].status.toLowerCase(), that.currentElement, calendar[i]);
                                    }
                                }
                            }

                            newSelectedEndDate = that.model.reverseFormatDate(date);

                            // If the selected end date is new or different, broadcast a changed event
                            if(typeof selectedEndDate === 'undefined' || newSelectedEndDate.toString() !== selectedEndDate.toString()) {
                                $(that.endCalendar).trigger('changed');
                            }
                            selectedEndDate = newSelectedEndDate;
                        },

                        /* Fires when the datepicker is closed.
                         * @param {String} dateText String representation of the date clicked on.
                         * @param {DatePicker} element The datepicker instance.
                         */
                        onClose: function(dateText, element) {
                            // Set the hover start date to undefined so that the hover dates won't be painted in the redraw
                            hoveredEndDate = undefined;
                            // Redraw the calendar to remove any hover coloring that may have been overlayed
                            $(that.endCalendar).datepicker('refresh').trigger('blur');

                            // If the user selects an end date that is less then the start date set the start date to the end date, update the first calendar
                            if(typeof selectedStartDate === 'undefined' || selectedStartDate > selectedEndDate) {
                                selectedStartDate = selectedEndDate;
                                $(that.el).datepicker('setDate', selectedStartDate);
                                $(that.el).trigger('changed');
                            }
                        }
                     });
                }

                // Once the datepicker is rendered append a loading class div and only display it if the endpoint is finished fetching data.
                $(this.el).append('<div class="calendar-loading" style="width: ' + $(this.el).width() + 'px; height: ' + $(this.el).height() + 'px; display:' + (this.fetching ? "block" : "none") + ';"></div>');
                return this;
            }
        });
    
        // Extend the default attributes with the options. This will get rid of any undefined vars.
        options = $.extend(true, {}, $.fn.calendar.defaults, options);

        this.model = new this.Model({
            defaultDate: options.defaultDate,
            numberOfMonths: options.numberOfMonths,
            propertyDruid: options.propertyDruid,
            defaultCalendar: options.calendar,
            defaultCalendarEvents: options.reservations,
            url: options.url,
            locale: options.locale
        });
        this.view = new this.View({
            el: options.el,
            model: this.model,
            clicked: options.clicked,
            error: options.error,
            fetch: options.fetch,
            hovered: options.hovered,
            endCalendar: options.endCalendar,
            hoverClass: options.hoverClass,
            hoverClassStart: options.hoverClassStart,
            hoverClassEnd: options.hoverClassEnd
        });
    };

    /* CALENDAR PLUGIN DEFINITION
    * ======================== */
    var old = $.fn.calendar;

    $.fn.calendar = function (option) {
        return this.each(function () {
            var $this = $(this), 
            data = $this.data('calendar'),
            options = typeof option == 'object' && option
            if(!options) options = {}
            options.el = $this
            if (!data) $this.data('calendar', (data = new Calendar(options)))
            if (typeof option == 'string') data[option]()
        });
    };

    $.fn.calendar.Constructor = Calendar;

    /* CALENDAR NO CONFLICT
    * ================== */
    $.fn.calendar.noConflict = function () {
        $.fn.calendar = old;
        return this;
    };

    // Defaults for the calendar.
    $.fn.calendar.defaults = {
        // The el to bind the end calendar to
        'endCalendar': undefined,
        // The class to add to hovered dates between two input date pickers
        'hoverClass': '',
        // The class to add to the start of hovered dates between two input date pickers
        'hoverClassStart': '',
        // The class to add to the end of hovered dates between two input date pickers
        'hoverClassEnd': '',
        // The locale for the calendar.
        'locale': 'en_US',
        // The default date to start the date picker for.
        'defaultDate': '',
        // The start and end dates to fetch data for.
        'endDate': '',
        'startDate': '',
        // Property druid to fetch data for.
        'propertyDruid': '',
        // The number of months to draw.
        'numberOfMonths': 1,
        // Should data be fetched.
        'fetch': true,
        // Default calendar days.
        'calendar': {},
        // Default calendar specific events for days.
        'reservations': {},
        'clicked': {
            'inquiry': function(element, reservation) {
            },
            'unavailable': function(element, reservation) {
            },
            'hold': function(element, reservation) {
            },
            'reserve': function(element, reservation) {
            },
            'delete': function(element, reservation) {
            }
        },
        'hovered': {
            'on': function(element, reservations) {
            },
            'off': function(element, reservations) {
            }
        },
        'error': function(error) {
        },
        'url': '/hai/availabilityCalendar/'
    };

}(window.jQuery, window.Backbone, window._);