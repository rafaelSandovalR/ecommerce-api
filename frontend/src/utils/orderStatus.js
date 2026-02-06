export const getStatusColor = (status) => {
    switch (status) {
        case 'DELIVERED': return 'bg-green-100 text-green-800';
        case 'SHIPPED': return 'bg-blue-100 text-blue-800';
        case 'PAID': return 'bg-purple-100 text-indigo-800';
        case 'CANCELLED': return 'bg-red-100 text-red-800';
        default: return 'bg-yellow-100 text-yellow-800'; // Pending/Processing   
    }
};