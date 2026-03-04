/**
 * Expense Service — JS module
 */
import { api } from '../core/api-client.js';

export const expenseService = {
    getAll:              ()        => api.get('/expense/getAll'),
    getById:             (id)      => api.get(`/expense/get/${id}`),
    add:                 (data)    => api.post('/expense/add', data),
    update:              (data)    => api.put('/expense/update', data),
    delete:              (id)      => api.delete(`/expense/delete/${id}`),
    getSummary:          ()        => api.get('/expense/summary'),
    getItemsByReport:    (id)      => api.get(`/expense/items/${id}`),
    addItem:             (data)    => api.post('/expense/items/add', data),
    deleteItem:          (id)      => api.delete(`/expense/items/delete/${id}`),
};

/**
 * Project Service — JS module
 */
export const projectService = {
    getAllProjects:       ()        => api.get('/project/getAll'),
    getProjectById:      (id)      => api.get(`/project/get/${id}`),
    addProject:          (data)    => api.post('/project/add', data),
    updateProject:       (data)    => api.put('/project/update', data),
    deleteProject:       (id)      => api.delete(`/project/delete/${id}`),
    getTasksByProject:   (id)      => api.get(`/project/tasks/${id}`),
    addTask:             (data)    => api.post('/project/tasks/add', data),
    updateTaskStatus:    (id, s)   => api.put(`/project/tasks/${id}/status`, { status: s }),
    deleteTask:          (id)      => api.delete(`/project/tasks/delete/${id}`),
};

/**
 * Chat Service — JS module
 */
export const chatService = {
    getMessages:         (sessionId) => api.get(`/chatbot/messages/${sessionId}`),
    sendMessage:         (data)      => api.post('/chatbot/send', data),
    clearSession:        (sessionId) => api.delete(`/chatbot/session/${sessionId}`),
    getSessions:         ()          => api.get('/chatbot/sessions'),
};
