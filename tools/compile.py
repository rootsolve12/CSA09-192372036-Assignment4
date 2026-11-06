from pathlib import Path
import subprocess,sys,json,datetime
R=Path(__file__).resolve().parents[1]
compiler=['java','-m','jdk.compiler/com.sun.tools.javac.Main','-g']
common=R/'build/common';common.mkdir(parents=True,exist_ok=True)
subprocess.run(compiler+['-d',str(common)]+list(map(str,(R/'tools/support').glob('*.java'))),check=True)
for n in ([int(sys.argv[1])] if len(sys.argv)>1 else range(1,7)):
 q=R/f'Q{n}';(q/'results').mkdir(exist_ok=True);(q/'screenshots').mkdir(exist_ok=True)
 groups=[('.',list(q.glob('*.java')))]
 if n==4:groups += [(x.name,[x/'DrawPad.java']) for x in (q/'variants').iterdir()]
 if n==5:groups += [(v,list((q/v).glob('*.java'))) for v in ['before','after','supplied']]
 logs=[]
 for variant,sources in groups:
  if not sources:continue
  dest=R/'build'/q.name/variant;dest.mkdir(parents=True,exist_ok=True)
  command=compiler+['-cp',str(common),'-d',str(dest)]+list(map(str,sources));p=subprocess.run(command,capture_output=True,text=True)
  expected=n==4 and variant=='WithOverride'
  logs+=['$ '+' '.join(command),p.stdout,p.stderr,f'Exit={p.returncode}; expected_failure={expected}']
  if (p.returncode!=0)!=expected:raise RuntimeError('\n'.join(logs))
 (q/'results/compiler.txt').write_text('\n'.join(logs))
 print(q.name,'compiled; expected override diagnostic checked' if n==4 else 'compiled')
(R/'build/environment.json').write_text(json.dumps({'java':subprocess.run(['java','-version'],capture_output=True,text=True).stderr,'origin':'workspace','compiled_utc':datetime.datetime.now(datetime.timezone.utc).isoformat()},indent=2))
