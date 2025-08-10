/**
 * EventHub - Modern Event Management System
 * Enhanced JavaScript functionality for improved user experience
 */

// Application namespace
const EventHub = {
    // Configuration
    config: {
        apiEndpoint: '/api',
        pageSize: 10,
        animationDuration: 300,
        debounceDelay: 300
    },

    // Initialize the application
    init: function() {
        this.bindEvents();
        this.initComponents();
        this.initAnimations();
        this.loadNotifications();
        this.initNavbarScrollEffect();
    },

    // Bind common events
    bindEvents: function() {
        // Handle form submissions
        document.addEventListener('submit', this.handleAjaxForm.bind(this));
        
        // Handle notification clicks
        document.addEventListener('click', this.handleNotificationClick.bind(this));
        
        // Handle search functionality
        document.addEventListener('input', this.handleSearch.bind(this));
        
        // Handle filter changes
        document.addEventListener('change', this.handleFilter.bind(this));

        // Handle floating action button
        document.addEventListener('click', this.handleFabClick.bind(this));

        // Handle card hover effects
        document.addEventListener('mouseenter', this.handleCardHover.bind(this));
        document.addEventListener('mouseleave', this.handleCardLeave.bind(this));
    },

    // Initialize modern components
    initComponents: function() {
        // Initialize tooltips
        if (typeof bootstrap !== 'undefined') {
            const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });

            // Initialize popovers
            const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
            popoverTriggerList.map(function (popoverTriggerEl) {
                return new bootstrap.Popover(popoverTriggerEl);
            });
        }

        // Initialize custom date pickers
        this.initDatePickers();
        
        // Initialize statistics animations
        this.initStatsAnimation();

        // Initialize lazy loading for images
        this.initLazyLoading();
    },

    // Initialize animations
    initAnimations: function() {
        // Observe elements for scroll animations
        if ('IntersectionObserver' in window) {
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add('animate-in');
                    }
                });
            }, {
                threshold: 0.1,
                rootMargin: '0px 0px -50px 0px'
            });

            // Observe elements with animation classes
            document.querySelectorAll('.fade-in, .slide-in-left, .slide-in-right').forEach(el => {
                observer.observe(el);
            });
        }
    },

    // Initialize navbar scroll effect
    initNavbarScrollEffect: function() {
        let lastScrollTop = 0;
        window.addEventListener('scroll', () => {
            const navbar = document.querySelector('.navbar');
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

            if (scrollTop > 100) {
                navbar.classList.add('navbar-scrolled');
            } else {
                navbar.classList.remove('navbar-scrolled');
            }

            lastScrollTop = scrollTop;
        });
    },

    // Initialize statistics animation
    initStatsAnimation: function() {
        const statNumbers = document.querySelectorAll('.stat-number');
        
        const animateNumber = (element) => {
            const target = parseInt(element.textContent);
            const duration = 2000;
            const steps = 60;
            const increment = target / steps;
            let current = 0;
            
            const timer = setInterval(() => {
                current += increment;
                element.textContent = Math.floor(current);
                
                if (current >= target) {
                    element.textContent = target;
                    clearInterval(timer);
                }
            }, duration / steps);
        };

        // Animate numbers when they come into view
        if ('IntersectionObserver' in window) {
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        animateNumber(entry.target);
                        observer.unobserve(entry.target);
                    }
                });
            });

            statNumbers.forEach(stat => observer.observe(stat));
        }
    },

    // Initialize date pickers
    initDatePickers: function() {
        const dateInputs = document.querySelectorAll('input[type="date"], input[type="datetime-local"]');
        dateInputs.forEach(input => {
            input.addEventListener('focus', function() {
                this.showPicker();
            });
        });
    },

    // Initialize lazy loading
    initLazyLoading: function() {
        if ('IntersectionObserver' in window) {
            const imageObserver = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const img = entry.target;
                        img.src = img.dataset.src;
                        img.classList.remove('lazy');
                        imageObserver.unobserve(img);
                    }
                });
            });

            document.querySelectorAll('img[data-src]').forEach(img => {
                imageObserver.observe(img);
            });
        }
    },

    // Handle AJAX form submissions
    handleAjaxForm: function(event) {
        if (!event.target.classList.contains('ajax-form')) return;
        
        event.preventDefault();
        const form = event.target;
        const formData = new FormData(form);
        
        // Show loading state
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processing...';
        submitBtn.disabled = true;

        fetch(form.action, {
            method: form.method,
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showToast('Success', data.message, 'success');
                if (data.redirect) {
                    setTimeout(() => window.location.href = data.redirect, 1000);
                }
            } else {
                this.showToast('Error', data.message, 'error');
            }
        })
        .catch(error => {
            this.showToast('Error', 'An unexpected error occurred', 'error');
        })
        .finally(() => {
            // Restore button state
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
    },

    // Handle notification clicks
    handleNotificationClick: function(event) {
        const notificationItem = event.target.closest('.notification-item');
        if (!notificationItem) return;

        if (notificationItem.classList.contains('unread')) {
            this.markNotificationAsRead(notificationItem);
        }
    },

    // Handle card hover effects
    handleCardHover: function(event) {
        const card = event.target.closest('.card, .stat-card, .event-card, .club-card');
        if (!card) return;

        card.style.transform = 'translateY(-8px) scale(1.02)';
    },

    handleCardLeave: function(event) {
        const card = event.target.closest('.card, .stat-card, .event-card, .club-card');
        if (!card) return;

        card.style.transform = '';
    },

    // Handle floating action button
    handleFabClick: function(event) {
        const fab = event.target.closest('.fab');
        if (!fab) return;

        event.preventDefault();
        
        // Add animation
        fab.style.transform = 'scale(1.2)';
        setTimeout(() => fab.style.transform = '', 150);

        // Show quick actions modal or menu
        this.showQuickActions();
    },

    // Show quick actions
    showQuickActions: function() {
        // This could show a modal with quick actions like:
        // - Create Event
        // - Join Club
        // - View Calendar
        // For now, just show a simple alert
        this.showToast('Quick Actions', 'Feature coming soon!', 'info');
    },

    // Handle search functionality
    handleSearch: function(event) {
        if (!event.target.classList.contains('search-input')) return;

        const searchTerm = event.target.value.toLowerCase();
        const searchables = document.querySelectorAll('.searchable');

        searchables.forEach(item => {
            const text = item.textContent.toLowerCase();
            const isVisible = text.includes(searchTerm);
            
            item.style.display = isVisible ? '' : 'none';
            
            // Add animation
            if (isVisible) {
                item.classList.add('fade-in');
            }
        });
    },

    // Handle filter changes
    handleFilter: function(event) {
        if (!event.target.classList.contains('filter-select')) return;

        const filterValue = event.target.value;
        const filterTarget = event.target.dataset.target;
        const items = document.querySelectorAll(filterTarget);

        items.forEach(item => {
            const shouldShow = !filterValue || item.dataset.category === filterValue;
            item.style.display = shouldShow ? '' : 'none';
            
            if (shouldShow) {
                item.classList.add('fade-in');
            }
        });
    },

    // Mark notification as read
    markNotificationAsRead: function(notificationElement) {
        const notificationId = notificationElement.dataset.id;
        
        fetch(`${this.config.apiEndpoint}/notifications/${notificationId}/read`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                notificationElement.classList.remove('unread');
                this.updateNotificationCount();
            }
        })
        .catch(error => console.error('Error marking notification as read:', error));
    },

    // Load notifications
    loadNotifications: function() {
        const notificationContainer = document.querySelector('.notification-container');
        if (!notificationContainer) return;

        fetch(`${this.config.apiEndpoint}/notifications`)
            .then(response => response.json())
            .then(data => {
                this.renderNotifications(data.notifications);
                this.updateNotificationCount(data.unreadCount);
            })
            .catch(error => console.error('Error loading notifications:', error));
    },

    // Render notifications
    renderNotifications: function(notifications) {
        const container = document.querySelector('.notification-container');
        if (!container) return;

        container.innerHTML = notifications.map(notification => `
            <div class="notification-item ${notification.read ? '' : 'unread'}" data-id="${notification.id}">
                <div class="d-flex">
                    <div class="me-3">
                        <i class="fas ${this.getNotificationIcon(notification.type)}"></i>
                    </div>
                    <div class="flex-grow-1">
                        <h6 class="mb-1">${notification.title}</h6>
                        <p class="mb-1">${notification.message}</p>
                        <small class="text-muted">${this.formatTime(notification.created_at)}</small>
                    </div>
                </div>
            </div>
        `).join('');
    },

    // Update notification count
    updateNotificationCount: function(count) {
        const badge = document.querySelector('.notification-badge');
        if (badge) {
            badge.textContent = count || 0;
            badge.style.display = count > 0 ? '' : 'none';
        }
    },

    // Get notification icon based on type
    getNotificationIcon: function(type) {
        const icons = {
            'event': 'fa-calendar-alt',
            'club': 'fa-users',
            'announcement': 'fa-bullhorn',
            'reminder': 'fa-bell'
        };
        return icons[type] || 'fa-info-circle';
    },

    // Format time for display
    formatTime: function(timestamp) {
        const now = new Date();
        const time = new Date(timestamp);
        const diff = now - time;
        
        if (diff < 60000) return 'Just now';
        if (diff < 3600000) return `${Math.floor(diff / 60000)}m ago`;
        if (diff < 86400000) return `${Math.floor(diff / 3600000)}h ago`;
        return `${Math.floor(diff / 86400000)}d ago`;
    },

    // Show modern toast notifications
    showToast: function(title, message, type = 'info') {
        const toastContainer = this.getToastContainer();
        const toastId = 'toast-' + Date.now();
        
        const toastHtml = `
            <div id="${toastId}" class="toast align-items-center text-white bg-${this.getToastColor(type)} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">
                        <strong><i class="fas ${this.getToastIcon(type)} me-2"></i>${title}</strong><br>
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;
        
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);
        
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement, {
            autohide: true,
            delay: 5000
        });
        
        toast.show();
        
        // Remove toast element after it's hidden
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    },

    // Get or create toast container
    getToastContainer: function() {
        let container = document.querySelector('.toast-container');
        if (!container) {
            container = document.createElement('div');
            container.className = 'toast-container position-fixed top-0 end-0 p-3';
            document.body.appendChild(container);
        }
        return container;
    },

    // Get toast color based on type
    getToastColor: function(type) {
        const colors = {
            'success': 'success',
            'error': 'danger',
            'warning': 'warning',
            'info': 'primary'
        };
        return colors[type] || 'primary';
    },

    // Get toast icon based on type
    getToastIcon: function(type) {
        const icons = {
            'success': 'fa-check-circle',
            'error': 'fa-exclamation-circle',
            'warning': 'fa-exclamation-triangle',
            'info': 'fa-info-circle'
        };
        return icons[type] || 'fa-info-circle';
    },

    // Utility function for debouncing
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    // Smooth scroll to element
    scrollTo: function(element, offset = 0) {
        const target = typeof element === 'string' ? document.querySelector(element) : element;
        if (!target) return;

        const targetPosition = target.offsetTop - offset;
        window.scrollTo({
            top: targetPosition,
            behavior: 'smooth'
        });
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    EventHub.init();
});

// Global utility functions
window.EventHub = EventHub;

// Export for modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = EventHub;
}
