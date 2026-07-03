import os
import re

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Remove lombok imports
    content = re.sub(r'import\s+lombok\.[^;]+;\s*', '', content)
    
    # Check if we need to modify
    needs_getter_setter = '@Getter' in content or '@Setter' in content
    needs_constructor = '@RequiredArgsConstructor' in content
    
    # Remove annotations
    content = re.sub(r'@Getter\s*|@Setter\s*', '', content)
    content = re.sub(r'@RequiredArgsConstructor\s*', '', content)
    content = re.sub(r'@Slf4j\s*', '', content)
    
    if needs_getter_setter or needs_constructor or 'log.info' in content:
        lines = content.split('\n')
        new_lines = []
        fields = []
        class_name = ""
        
        for line in lines:
            if line.startswith('public class '):
                class_name = line.split('public class ')[1].split(' ')[0]
                if 'log.info' in content:
                    new_lines.append(line)
                    new_lines.append(f'    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger({class_name}.class);')
                    continue
            
            # Find fields
            m = re.match(r'^\s*(private|protected|public)\s+(final\s+)?([A-Za-z0-9<>_]+)\s+([A-Za-z0-9_]+)\s*;', line)
            if m:
                fields.append((m.group(3), m.group(4), m.group(2) is not None))
            new_lines.append(line)
            
        content = '\n'.join(new_lines)
        
        # Inject constructor if required
        if needs_constructor and class_name:
            final_fields = [f for f in fields if f[2]]
            if final_fields:
                params = ", ".join([f"{f[0]} {f[1]}" for f in final_fields])
                assignments = "\n".join([f"        this.{f[1]} = {f[1]};" for f in final_fields])
                constructor = f"\n    public {class_name}({params}) {{\n{assignments}\n    }}\n"
                
                # insert before last closing brace
                content = content.rstrip()
                if content.endswith('}'):
                    content = content[:-1] + constructor + '}'
                    
        # Inject getters and setters
        if needs_getter_setter:
            methods = []
            for f in fields:
                type_name, var_name, _ = f
                capitalized = var_name[0].upper() + var_name[1:]
                methods.append(f"    public {type_name} get{capitalized}() {{ return this.{var_name}; }}")
                methods.append(f"    public void set{capitalized}({type_name} {var_name}) {{ this.{var_name} = {var_name}; }}")
            
            methods_str = "\n".join(methods)
            content = content.rstrip()
            if content.endswith('}'):
                content = content[:-1] + "\n" + methods_str + "\n}"
                
        with open(filepath, 'w') as f:
            f.write(content)

for root, dirs, files in os.walk('src'):
    for file in files:
        if file.endswith('.java'):
            process_file(os.path.join(root, file))
