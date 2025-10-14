from flask import Blueprint, request, jsonify
from src.config import supabase

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        username = data.get('username')
        password = data.get('password')
        
        if not username or not password:
            return jsonify({'error': 'Username and password are required'}), 400
        
        # Query user from Supabase
        response = supabase.table('users').select('*').eq('username', username).eq('password', password).execute()
        
        if not response.data or len(response.data) == 0:
            return jsonify({'error': 'Invalid credentials'}), 401
        
        user = response.data[0]
        
        # Remove password from response
        user.pop('password', None)
        
        return jsonify({
            'message': 'Login successful',
            'user': user
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@auth_bp.route('/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        username = data.get('username')
        password = data.get('password')
        name = data.get('name')
        email = data.get('email')
        
        if not all([username, password, name, email]):
            return jsonify({'error': 'All fields are required'}), 400
        
        # Check if user already exists
        existing_user = supabase.table('users').select('*').eq('username', username).execute()
        if existing_user.data and len(existing_user.data) > 0:
            return jsonify({'error': 'Username already exists'}), 409
        
        # Check if email already exists
        existing_email = supabase.table('users').select('*').eq('email', email).execute()
        if existing_email.data and len(existing_email.data) > 0:
            return jsonify({'error': 'Email already exists'}), 409
        
        # Insert new user
        response = supabase.table('users').insert({
            'username': username,
            'password': password,
            'name': name,
            'email': email
        }).execute()
        
        user = response.data[0]
        user.pop('password', None)
        
        return jsonify({
            'message': 'Registration successful',
            'user': user
        }), 201
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

