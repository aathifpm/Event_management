/**
 * College Event Management System - Main JavaScript File
 * Handles common functionality across the application
 */

// Application namespace
const EventManagement = {
    // Configuration
    config: {
        apiEndpoint: '/api',
        pageSize: 10,
        animationDuration: 300
    },

    // Initialize the application
    init: function() {
        this.bindEvents();
        this.initComponents();
        this.loadNotifications();
    },

    // Bind common events
    bindEvents: function() {
        // Handle form submissions
        $(document).on('submit', '.ajax-form', this.handleAjaxForm);
        
        // Handle notification clicks
        $(document).on('click', '.notification-item', this.markNotificationAsRead);
        
        // Handle modal forms
        $(document).on('shown.bs.modal', '.modal', this.focusFirstInput);
        
        // Handle search functionality
        $(document).on('input', '.search-input', this.handleSearch);
        
        // Handle filter changes
        $(document).on('change', '.filter-select', this.handleFilter);
    },

    // Initialize components
    initComponents: function() {
        // Initialize tooltips
        if (typeof bootstrap !== 'undefined') {
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
        }

        // Initialize date pickers
        this.initDatePickers();
        
        // Initialize rich text editors
        this.initRichTextEditors();
        
        // Add animation classes to elements
        this.addAnimations();
    },

    // Handle AJAX form submissions
    handleAjaxForm: function(e) {
        e.preventDefault();
        const form = $(this);
        const url = form.attr('action');
        const method = form.attr('method') || 'POST';
        const data = form.serialize();

        EventManagement.showLoader();

        $.ajax({
            url: url,
            method: method,
            data: data,
            success: function(response) {
                EventManagement.hideLoader();
                if (response.success) {
                    EventManagement.showNotification('Success!', response.message, 'success');
                    if (response.redirect) {
                        window.location.href = response.redirect;
                    } else {
                        form[0].reset();
                    }
                } else {
                    EventManagement.showNotification('Error!', response.message, 'error');
                }
            },
            error: function(xhr, status, error) {
                EventManagement.hideLoader();
                EventManagement.showNotification('Error!', 'An error occurred. Please try again.', 'error');
            }
        });
    },

    // Mark notification as read
    markNotificationAsRead: function() {
        const notificationId = $(this).data('notification-id');
        if (notificationId) {
            $.post('/api/notifications/' + notificationId + '/mark-read', function(response) {
                if (response.success) {
                    $('.notification-item[data-notification-id="' + notificationId + '"]')
                        .removeClass('unread')
                        .addClass('read');
                }
            });
        }
    },

    // Focus first input in modal
    focusFirstInput: function() {
        $(this).find('input:text:visible:first').focus();
    },

    // Handle search functionality
    handleSearch: function() {
        const searchTerm = $(this).val().toLowerCase();
        const targetContainer = $(this).data('target');
        
        $(targetContainer + ' .searchable-item').each(function() {
            const text = $(this).text().toLowerCase();
            if (text.includes(searchTerm)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    },

    // Handle filter functionality
    handleFilter: function() {
        const filterValue = $(this).val();
        const targetContainer = $(this).data('target');
        const filterAttribute = $(this).data('filter');
        
        if (filterValue === 'all') {
            $(targetContainer + ' .filterable-item').show();
        } else {
            $(targetContainer + ' .filterable-item').each(function() {
                const itemValue = $(this).data(filterAttribute);
                if (itemValue === filterValue) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        }
    },

    // Initialize date pickers
    initDatePickers: function() {
        $('.datepicker').each(function() {
            // Add date picker functionality if available
            if (typeof flatpickr !== 'undefined') {
                flatpickr(this, {
                    dateFormat: "Y-m-d",
                    minDate: "today"
                });
            }
        });

        $('.datetimepicker').each(function() {
            if (typeof flatpickr !== 'undefined') {
                flatpickr(this, {
                    enableTime: true,
                    dateFormat: "Y-m-d H:i",
                    minDate: "today"
                });
            }
        });
    },

    // Initialize rich text editors
    initRichTextEditors: function() {
        $('.rich-text-editor').each(function() {
            // Add rich text editor functionality if available
            if (typeof tinymce !== 'undefined') {
                tinymce.init({
                    selector: this,
                    height: 300,
                    menubar: false,
                    plugins: 'link lists',
                    toolbar: 'bold italic underline | alignleft aligncenter alignright | bullist numlist | link'
                });
            }
        });
    },

    // Add animations to elements
    addAnimations: function() {
        // Add fade-in animation to cards
        $('.card').addClass('fade-in');
        
        // Add slide-in animations based on position
        $('.slide-left').addClass('slide-in-left');
        $('.slide-right').addClass('slide-in-right');
    },

    // Show loading spinner
    showLoader: function() {
        if ($('.spinner-overlay').length === 0) {
            $('body').append(`
                <div class="spinner-overlay">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            `);
        }
        $('.spinner-overlay').show();
    },

    // Hide loading spinner
    hideLoader: function() {
        $('.spinner-overlay').hide();
    },

    // Show notification
    showNotification: function(title, message, type = 'info') {
        const alertClass = 'alert-' + (type === 'error' ? 'danger' : type);
        const notification = `
            <div class="alert ${alertClass} alert-dismissible fade show position-fixed" 
                 style="top: 20px; right: 20px; z-index: 10000; min-width: 300px;">
                <strong>${title}</strong> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        
        $('body').append(notification);
        
        // Auto-remove after 5 seconds
        setTimeout(function() {
            $('.alert').alert('close');
        }, 5000);
    },

    // Load notifications
    loadNotifications: function() {
        // Load and display recent notifications
        $.get('/api/notifications/recent', function(notifications) {
            if (notifications && notifications.length > 0) {
                EventManagement.updateNotificationBadge(notifications.length);
                EventManagement.populateNotificationDropdown(notifications);
            }
        });
    },

    // Update notification badge
    updateNotificationBadge: function(count) {
        const badge = $('.notification-badge');
        if (count > 0) {
            badge.text(count).show();
        } else {
            badge.hide();
        }
    },

    // Populate notification dropdown
    populateNotificationDropdown: function(notifications) {
        const dropdown = $('.notification-dropdown');
        dropdown.empty();
        
        notifications.forEach(function(notification) {
            const item = `
                <div class="notification-item p-3 border-bottom" data-notification-id="${notification.id}">
                    <h6 class="mb-1">${notification.title}</h6>
                    <p class="mb-1 text-muted small">${notification.message}</p>
                    <small class="text-muted">${EventManagement.formatDate(notification.dateSent)}</small>
                </div>
            `;
            dropdown.append(item);
        });
    },

    // Format date
    formatDate: function(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diffInMinutes = Math.floor((now - date) / (1000 * 60));
        
        if (diffInMinutes < 1) return 'Just now';
        if (diffInMinutes < 60) return `${diffInMinutes} minutes ago`;
        if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)} hours ago`;
        if (diffInMinutes < 10080) return `${Math.floor(diffInMinutes / 1440)} days ago`;
        
        return date.toLocaleDateString();
    },

    // Confirm action
    confirmAction: function(message, callback) {
        if (confirm(message)) {
            callback();
        }
    },

    // Event registration functionality
    registerForEvent: function(eventId, callback) {
        $.post('/api/events/' + eventId + '/register', function(response) {
            if (response.success) {
                EventManagement.showNotification('Success!', 'Successfully registered for the event!', 'success');
                if (callback) callback(response);
            } else {
                EventManagement.showNotification('Error!', response.message, 'error');
            }
        });
    },

    // Event unregistration functionality
    unregisterFromEvent: function(eventId, callback) {
        EventManagement.confirmAction('Are you sure you want to unregister from this event?', function() {
            $.ajax({
                url: '/api/events/' + eventId + '/unregister',
                method: 'DELETE',
                success: function(response) {
                    if (response.success) {
                        EventManagement.showNotification('Success!', 'Successfully unregistered from the event!', 'success');
                        if (callback) callback(response);
                    } else {
                        EventManagement.showNotification('Error!', response.message, 'error');
                    }
                }
            });
        });
    },

    // Join club functionality
    joinClub: function(clubId, callback) {
        $.post('/api/clubs/' + clubId + '/join', function(response) {
            if (response.success) {
                EventManagement.showNotification('Success!', 'Successfully joined the club!', 'success');
                if (callback) callback(response);
            } else {
                EventManagement.showNotification('Error!', response.message, 'error');
            }
        });
    },

    // Leave club functionality
    leaveClub: function(clubId, callback) {
        EventManagement.confirmAction('Are you sure you want to leave this club?', function() {
            $.ajax({
                url: '/api/clubs/' + clubId + '/leave',
                method: 'DELETE',
                success: function(response) {
                    if (response.success) {
                        EventManagement.showNotification('Success!', 'Successfully left the club!', 'success');
                        if (callback) callback(response);
                    } else {
                        EventManagement.showNotification('Error!', response.message, 'error');
                    }
                }
            });
        });
    }
};

// Initialize the application when document is ready
$(document).ready(function() {
    EventManagement.init();
});

// Export for global access
window.EventManagement = EventManagement;
