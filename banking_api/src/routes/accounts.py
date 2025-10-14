from flask import Blueprint, request, jsonify
from src.config import supabase
import uuid
import random

accounts_bp = Blueprint('accounts', __name__)

def generate_account_number():
    """Generate a random account number"""
    return f"{random.randint(10000000, 99999999)}"

@accounts_bp.route('/accounts', methods=['GET'])
def get_accounts():
    try:
        user_id = request.args.get('user_id')
        
        if not user_id:
            return jsonify({'error': 'User ID is required'}), 400
        
        # Get all accounts for the user
        response = supabase.table('accounts').select('*').eq('user_id', user_id).execute()
        
        return jsonify({
            'accounts': response.data
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts', methods=['POST'])
def create_account():
    try:
        data = request.get_json()
        user_id = data.get('user_id')
        account_type = data.get('account_type', 'CHECKING')
        interest_strategy = data.get('interest_strategy')
        
        if not user_id:
            return jsonify({'error': 'User ID is required'}), 400
        
        if account_type not in ['CHECKING', 'SAVINGS']:
            return jsonify({'error': 'Invalid account type'}), 400
        
        # Generate unique account number
        account_number = generate_account_number()
        
        # Prepare account data
        account_data = {
            'user_id': user_id,
            'account_number': account_number,
            'account_type': account_type,
            'balance': 0.00
        }
        
        # Add interest strategy for savings accounts
        if account_type == 'SAVINGS':
            if interest_strategy not in ['SIMPLE', 'HIGH_YIELD']:
                interest_strategy = 'SIMPLE'
            account_data['interest_strategy'] = interest_strategy
        
        # Insert new account
        response = supabase.table('accounts').insert(account_data).execute()
        
        return jsonify({
            'message': 'Account created successfully',
            'account': response.data[0]
        }), 201
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts/<account_id>', methods=['GET'])
def get_account(account_id):
    try:
        response = supabase.table('accounts').select('*').eq('id', account_id).execute()
        
        if not response.data or len(response.data) == 0:
            return jsonify({'error': 'Account not found'}), 404
        
        return jsonify({
            'account': response.data[0]
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts/<account_id>/deposit', methods=['POST'])
def deposit(account_id):
    try:
        data = request.get_json()
        amount = float(data.get('amount', 0))
        
        if amount <= 0:
            return jsonify({'error': 'Amount must be greater than 0'}), 400
        
        # Get current account
        account_response = supabase.table('accounts').select('*').eq('id', account_id).execute()
        
        if not account_response.data or len(account_response.data) == 0:
            return jsonify({'error': 'Account not found'}), 404
        
        account = account_response.data[0]
        new_balance = float(account['balance']) + amount
        
        # Update balance
        supabase.table('accounts').update({'balance': new_balance}).eq('id', account_id).execute()
        
        # Record transaction
        supabase.table('transactions').insert({
            'account_id': account_id,
            'transaction_type': 'DEPOSIT',
            'amount': amount,
            'description': f'Deposit of ${amount:.2f}'
        }).execute()
        
        # Record audit log
        supabase.table('audit_logs').insert({
            'account_id': account_id,
            'event_type': 'DEPOSIT',
            'event_data': f'Deposited ${amount:.2f}. New balance: ${new_balance:.2f}'
        }).execute()
        
        return jsonify({
            'message': 'Deposit successful',
            'new_balance': new_balance
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts/<account_id>/withdraw', methods=['POST'])
def withdraw(account_id):
    try:
        data = request.get_json()
        amount = float(data.get('amount', 0))
        
        if amount <= 0:
            return jsonify({'error': 'Amount must be greater than 0'}), 400
        
        # Get current account
        account_response = supabase.table('accounts').select('*').eq('id', account_id).execute()
        
        if not account_response.data or len(account_response.data) == 0:
            return jsonify({'error': 'Account not found'}), 404
        
        account = account_response.data[0]
        current_balance = float(account['balance'])
        
        if current_balance < amount:
            return jsonify({'error': 'Insufficient funds'}), 400
        
        new_balance = current_balance - amount
        
        # Update balance
        supabase.table('accounts').update({'balance': new_balance}).eq('id', account_id).execute()
        
        # Record transaction
        supabase.table('transactions').insert({
            'account_id': account_id,
            'transaction_type': 'WITHDRAWAL',
            'amount': amount,
            'description': f'Withdrawal of ${amount:.2f}'
        }).execute()
        
        # Record audit log
        supabase.table('audit_logs').insert({
            'account_id': account_id,
            'event_type': 'WITHDRAWAL',
            'event_data': f'Withdrew ${amount:.2f}. New balance: ${new_balance:.2f}'
        }).execute()
        
        return jsonify({
            'message': 'Withdrawal successful',
            'new_balance': new_balance
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts/<account_id>/transfer', methods=['POST'])
def transfer(account_id):
    try:
        data = request.get_json()
        target_account_id = data.get('target_account_id')
        amount = float(data.get('amount', 0))
        
        if not target_account_id:
            return jsonify({'error': 'Target account ID is required'}), 400
        
        if amount <= 0:
            return jsonify({'error': 'Amount must be greater than 0'}), 400
        
        # Get source account
        source_response = supabase.table('accounts').select('*').eq('id', account_id).execute()
        
        if not source_response.data or len(source_response.data) == 0:
            return jsonify({'error': 'Source account not found'}), 404
        
        source_account = source_response.data[0]
        source_balance = float(source_account['balance'])
        
        if source_balance < amount:
            return jsonify({'error': 'Insufficient funds'}), 400
        
        # Get target account
        target_response = supabase.table('accounts').select('*').eq('id', target_account_id).execute()
        
        if not target_response.data or len(target_response.data) == 0:
            return jsonify({'error': 'Target account not found'}), 404
        
        target_account = target_response.data[0]
        target_balance = float(target_account['balance'])
        
        # Update balances
        new_source_balance = source_balance - amount
        new_target_balance = target_balance + amount
        
        supabase.table('accounts').update({'balance': new_source_balance}).eq('id', account_id).execute()
        supabase.table('accounts').update({'balance': new_target_balance}).eq('id', target_account_id).execute()
        
        # Record transactions
        supabase.table('transactions').insert({
            'account_id': account_id,
            'transaction_type': 'TRANSFER_OUT',
            'amount': amount,
            'description': f'Transfer to account {target_account["account_number"]}'
        }).execute()
        
        supabase.table('transactions').insert({
            'account_id': target_account_id,
            'transaction_type': 'TRANSFER_IN',
            'amount': amount,
            'description': f'Transfer from account {source_account["account_number"]}'
        }).execute()
        
        # Record audit logs
        supabase.table('audit_logs').insert({
            'account_id': account_id,
            'event_type': 'TRANSFER_OUT',
            'event_data': f'Transferred ${amount:.2f} to {target_account["account_number"]}. New balance: ${new_source_balance:.2f}'
        }).execute()
        
        supabase.table('audit_logs').insert({
            'account_id': target_account_id,
            'event_type': 'TRANSFER_IN',
            'event_data': f'Received ${amount:.2f} from {source_account["account_number"]}. New balance: ${new_target_balance:.2f}'
        }).execute()
        
        return jsonify({
            'message': 'Transfer successful',
            'new_balance': new_source_balance
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts/<account_id>/calculate-interest', methods=['POST'])
def calculate_interest(account_id):
    try:
        # Get account
        account_response = supabase.table('accounts').select('*').eq('id', account_id).execute()
        
        if not account_response.data or len(account_response.data) == 0:
            return jsonify({'error': 'Account not found'}), 404
        
        account = account_response.data[0]
        
        if account['account_type'] != 'SAVINGS':
            return jsonify({'error': 'Interest can only be calculated for savings accounts'}), 400
        
        current_balance = float(account['balance'])
        interest_strategy = account.get('interest_strategy', 'SIMPLE')
        
        # Calculate interest based on strategy
        if interest_strategy == 'SIMPLE':
            interest_rate = 0.02  # 2%
        else:  # HIGH_YIELD
            interest_rate = 0.05  # 5%
        
        interest = current_balance * interest_rate
        new_balance = current_balance + interest
        
        # Update balance
        supabase.table('accounts').update({'balance': new_balance}).eq('id', account_id).execute()
        
        # Record transaction
        supabase.table('transactions').insert({
            'account_id': account_id,
            'transaction_type': 'INTEREST',
            'amount': interest,
            'description': f'Interest calculated ({interest_strategy}): ${interest:.2f}'
        }).execute()
        
        # Record audit log
        supabase.table('audit_logs').insert({
            'account_id': account_id,
            'event_type': 'INTEREST_CALCULATED',
            'event_data': f'Interest calculated ({interest_strategy}): ${interest:.2f}. New balance: ${new_balance:.2f}'
        }).execute()
        
        return jsonify({
            'message': 'Interest calculated successfully',
            'interest': interest,
            'new_balance': new_balance
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@accounts_bp.route('/accounts/<account_id>/transactions', methods=['GET'])
def get_transactions(account_id):
    try:
        # Get all transactions for the account
        response = supabase.table('transactions').select('*').eq('account_id', account_id).order('created_at', desc=True).execute()
        
        return jsonify({
            'transactions': response.data
        }), 200
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

