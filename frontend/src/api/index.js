/**
 * API 接口封装
 */

import { server } from '../config';

// 使用配置中的API基础地址
const API_BASE_URL = server.apiBaseUrl;

// 获取认证token
const getAuthHeaders = () => {
  const headers = {
    'Content-Type': 'application/json'
  }
  // 从sessionStorage获取token
  const userData = sessionStorage.getItem('currentUser')
  if (userData) {
    try {
      const user = JSON.parse(userData)
      if (user.token) {
        headers['Authorization'] = `Bearer ${user.token}`
      }
    } catch (e) {
      console.error('解析用户数据失败', e)
    }
  }
  return headers
}

// 获取验证码 - 支持绑定用户名
export const getCaptcha = async (username) => {
  try {
    let url = `${API_BASE_URL}/auth/captcha`
    if (username) {
      url += `?username=${encodeURIComponent(username)}`
    }
    console.log('获取验证码API调用开始，URL:', url);
    const response = await fetch(url);
    console.log('获取验证码API响应状态:', response.status);
    const data = await response.json();
    console.log('获取验证码API响应数据:', data);
    // 后端返回 { success: true, data: { code, timestamp } }
    if (data.success && data.data) {
      return data.data
    }
    return { code: null }
  } catch (error) {
    console.error('获取验证码失败:', error);
    return { code: null };
  }
};

// 检查账号是否已存在
export const checkUsername = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/check-username`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username }),
    });
    return await response.json();
  } catch (error) {
    console.error('检查账号失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 用户注册 - 验证码已绑定用户名
export const register = async (username, password, captcha, nickname) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password, captcha, nickname }),
    });
    return await response.json();
  } catch (error) {
    console.error('注册失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 用户登录 - 验证码已绑定用户名
export const login = async (username, password, captcha) => {
  try {
    console.log('登录API调用开始，URL:', `${API_BASE_URL}/auth/login`);
    console.log('登录参数:', { username, password, captcha });
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password, captcha }),
    });
    console.log('登录API响应状态:', response.status);
    const data = await response.json();
    console.log('登录API响应数据:', data);
    return data;
  } catch (error) {
    console.error('登录失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 修改密码
export const changePassword = async (username, newPassword, confirmPassword) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/change-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, newPassword, confirmPassword }),
    });
    return await response.json();
  } catch (error) {
    console.error('修改密码失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取用户信息
export const getUserInfo = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/user/info?username=${encodeURIComponent(username)}`);
    return await response.json();
  } catch (error) {
    console.error('获取用户信息失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 更新用户信息
export const updateUserInfo = async (username, nickname, email, phone, avatar) => {
  try {
    const response = await fetch(`${API_BASE_URL}/user/update`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, nickname, email, phone, avatar }),
    });
    return await response.json();
  } catch (error) {
    console.error('更新用户信息失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 修改密码（验证旧密码）
export const changePasswordWithOld = async (username, oldPassword, newPassword) => {
  try {
    const response = await fetch(`${API_BASE_URL}/user/password`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, oldPassword, newPassword }),
    });
    return await response.json();
  } catch (error) {
    console.error('修改密码失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// ===== 管理端 API =====

// 获取所有用户列表
export const getAdminUsers = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/users`, {
      headers: getAuthHeaders()
    });
    return await response.json();
  } catch (error) {
    console.error('获取用户列表失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取待审批的权限申请
export const getPendingRequests = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/pending-requests`, {
      headers: getAuthHeaders()
    });
    return await response.json();
  } catch (error) {
    console.error('获取申请列表失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 审批权限申请
export const approveRequest = async (userId, approved) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/approve-request`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ userId, approved }),
    });
    return await response.json();
  } catch (error) {
    console.error('审批失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 申请成为管理员
export const applyAdmin = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/apply`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username }),
    });
    return await response.json();
  } catch (error) {
    console.error('申请失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 撤销管理员申请
export const cancelApplication = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/cancel-application`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ username }),
    });
    return await response.json();
  } catch (error) {
    console.error('撤销申请失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取用户权限状态
export const getUserAdminStatus = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/user-status?username=${encodeURIComponent(username)}`);
    return await response.json();
  } catch (error) {
    console.error('获取状态失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 撤销管理员权限
export const revokeAdmin = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/revoke`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ userId }),
    });
    return await response.json();
  } catch (error) {
    console.error('撤销权限失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取统计信息
export const getAdminStats = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/stats`, {
      headers: getAuthHeaders()
    });
    return await response.json();
  } catch (error) {
    console.error('获取统计信息失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 更新用户状态
export const updateUserStatus = async (userId, status) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/update-user-status`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ userId, status }),
    });
    return await response.json();
  } catch (error) {
    console.error('更新状态失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 删除用户
export const deleteUser = async (userId) => {
  try {
    const response = await fetch(`${API_BASE_URL}/admin/delete-user`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ userId }),
    });
    return await response.json();
  } catch (error) {
    console.error('删除用户失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// ===== 商品管理 API =====

// 获取商品列表
export const getProducts = async (category, keyword) => {
  try {
    let url = `${API_BASE_URL}/products`
    const params = []
    if (category && category !== 'all') params.push(`category=${category}`)
    if (keyword) params.push(`keyword=${encodeURIComponent(keyword)}`)
    // 设置足够大的size来获取所有商品
    params.push(`page=1`)
    params.push(`size=1000`)
    if (params.length > 0) url += '?' + params.join('&')
    
    const response = await fetch(url);
    return await response.json();
  } catch (error) {
    console.error('获取商品列表失败:', error);
    return { success: false, message: '服务器错误', products: [] };
  }
};

// 获取单个商品
export const getProduct = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/products/${id}`);
    return await response.json();
  } catch (error) {
    console.error('获取商品失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 添加商品
export const addProduct = async (productData) => {
  try {
    const response = await fetch(`${API_BASE_URL}/products`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(productData),
    });
    return await response.json();
  } catch (error) {
    console.error('添加商品失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 更新商品
export const updateProduct = async (id, productData) => {
  try {
    const response = await fetch(`${API_BASE_URL}/products/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(productData),
    });
    return await response.json();
  } catch (error) {
    console.error('更新商品失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 删除商品
export const deleteProduct = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/products/${id}`, {
      method: 'DELETE',
    });
    return await response.json();
  } catch (error) {
    console.error('删除商品失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取销售统计
export const getSalesStats = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/products/stats`);
    return await response.json();
  } catch (error) {
    console.error('获取销售统计失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取订单统计
export const getOrderStats = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/orders/stats`);
    return await response.json();
  } catch (error) {
    console.error('获取订单统计失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 更新库存
export const updateStock = async (id, stock) => {
  try {
    const response = await fetch(`${API_BASE_URL}/products/${id}/stock`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ stock }),
    });
    return await response.json();
  } catch (error) {
    console.error('更新库存失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// ===== 客服消息 API =====

// 获取用户客服消息
export const getCustomerMessages = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/customer/messages?username=${encodeURIComponent(username)}`);
    return await response.json();
  } catch (error) {
    console.error('获取客服消息失败:', error);
    return { success: false, message: '服务器错误', messages: [] };
  }
};

// 发送客服消息
export const sendCustomerMessage = async (data) => {
  try {
    const response = await fetch(`${API_BASE_URL}/customer/send`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    return await response.json();
  } catch (error) {
    console.error('发送客服消息失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 获取所有客服消息（管理员用）
export const getAllCustomerMessages = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/customer/all-messages`)
    const data = await response.json()
    return data
  } catch (error) {
    console.error('获取所有客服消息失败:', error)
    return { success: false, message: error.message, conversations: [] }
  }
};

// 标记消息为已读
export const markMessageAsRead = async (username) => {
  try {
    const response = await fetch(`${API_BASE_URL}/customer/mark-read`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username }),
    });
    return await response.json();
  } catch (error) {
    console.error('标记消息已读失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// 发送客服回复（管理员用）
export const sendCustomerReply = async (data) => {
  try {
    const response = await fetch(`${API_BASE_URL}/customer/reply`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
    return await response.json();
  } catch (error) {
    console.error('发送客服回复失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// ===== 订单管理 API =====

// 获取订单列表
export const getOrders = async (page = 1, size = 10, keyword = '', status = '') => {
  try {
    let url = `${API_BASE_URL}/orders?page=${page}&size=${size}`
    if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`
    if (status) url += `&status=${encodeURIComponent(status)}`
    
    const response = await fetch(url, {
      headers: getAuthHeaders()
    });
    return await response.json();
  } catch (error) {
    console.error('获取订单列表失败:', error);
    return { success: false, message: '服务器错误', data: null };
  }
};

// 获取订单详情
export const getOrderDetail = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/orders/${id}`, {
      headers: getAuthHeaders()
    });
    return await response.json();
  } catch (error) {
    console.error('获取订单详情失败:', error);
    return { success: false, message: '服务器错误', data: null };
  }
};

// 更新订单状态
export const updateOrderStatus = async (id, status) => {
  try {
    const response = await fetch(`${API_BASE_URL}/orders/${id}/status`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify({ status }),
    });
    return await response.json();
  } catch (error) {
    console.error('更新订单状态失败:', error);
    return { success: false, message: '服务器错误' };
  }
};

// ===== 商品评论 API =====

// 获取商品评论列表
export const getProductReviews = async (productId, page = 1, size = 100) => {
  try {
    const response = await fetch(`${API_BASE_URL}/reviews/product/${productId}?page=${page}&size=${size}`);
    return await response.json();
  } catch (error) {
    console.error('获取商品评论失败:', error);
    return { success: false, message: '服务器错误', reviews: [] };
  }
};

// 添加商品评论
export const addProductReview = async (productId, username, rating, content) => {
  try {
    const response = await fetch(`${API_BASE_URL}/reviews/add`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ productId, username, rating, content }),
    });
    return await response.json();
  } catch (error) {
    console.error('添加商品评论失败:', error);
    return { success: false, message: '服务器错误' };
  }
};
