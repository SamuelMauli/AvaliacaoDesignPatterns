import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

from src.config import supabase

# Read the SQL file
with open('init_db.sql', 'r') as f:
    sql_content = f.read()

# Split the SQL into individual statements
statements = [stmt.strip() for stmt in sql_content.split(';') if stmt.strip() and not stmt.strip().startswith('--')]

print("Initializing database schema...")

for i, statement in enumerate(statements, 1):
    try:
        # Skip comments and empty statements
        if statement.startswith('--') or not statement.strip():
            continue
        
        print(f"\nExecuting statement {i}...")
        print(f"Statement: {statement[:100]}...")
        
        # Execute the SQL statement using Supabase RPC or direct query
        result = supabase.rpc('exec_sql', {'query': statement}).execute()
        print(f"✓ Statement {i} executed successfully")
        
    except Exception as e:
        # Try using postgrest query if RPC fails
        try:
            result = supabase.postgrest.rpc('exec_sql', {'query': statement}).execute()
            print(f"✓ Statement {i} executed successfully (via postgrest)")
        except Exception as e2:
            print(f"✗ Error executing statement {i}: {str(e)}")
            print(f"  Alternative attempt also failed: {str(e2)}")
            # Continue with other statements
            continue

print("\n✓ Database initialization complete!")

