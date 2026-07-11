document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const btn = alert.querySelector('.btn-close');
            if (btn) {
                btn.click();
            }
        }, 4000);
    });
});