<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
	<style>
		#textoConteudo {
			line-height: 150%;
			font-family: serif;
			font-size: 12pt;
		}
	</style>    
	<c:if test="${requestScope.liberaEmissao == true}"> <%-- seguran�a importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>
		<div id="textoConteudo">	
			<h2><u>ANEXO I</u></h2>
			<h2>		
				TERMO DE COMPROMISSO DO ESTAGI�RIO PARA A REALIZA��O DE EST�GIO CURRICULAR SUPERVISIONADO
			</h2>	
			<h3>${estagioMBean.obj.tipoEstagio.descricao}</h3>
			<p style="margin-left: 0.5cm; margin-bottom: 0cm">&nbsp;</p>
			<p style="margin-bottom: 0cm">
			   (Instrumento decorrente do Conv�nio n&ordm; 
			   ${estagioMBean.obj.concedente.convenioEstagio.numeroConvenio} / ${ configSistema['siglaInstituicao'] })
			</p>
			<p style="margin-left: 0.5cm; margin-bottom: 0cm">&nbsp;</p>
			<p style="text-align: justify; text-indent: 25px;">
				Pelo presente Instrumento, o(a) estudante ${estagioMBean.obj.discente.nome}, 
				do ${estagioMBean.obj.discente.periodoAtual}&deg; Per�odo do Curso de ${estagioMBean.obj.discente.curso.nome}, 
				matr�cula n&deg; ${estagioMBean.obj.discente.matricula}, RG n&deg; ${estagioMBean.obj.discente.pessoa.identidade}, 
				CPF n&deg; ${estagioMBean.obj.discente.pessoa.cpfCnpjFormatado}, regularmente matriculado e com efetiva frequ�ncia 
				doravante denominado ESTAGI�RIO e ${estagioMBean.obj.concedente.pessoa.nome}, 
				doravante denominado CONCEDENTE, representado(a) por seu ${estagioMBean.obj.concedente.responsavel.cargo},
				o(a) Sr(a). ${estagioMBean.obj.concedente.responsavel.pessoa.nome}, portador do Registro Geral n&deg; 
				${estagioMBean.obj.concedente.responsavel.pessoa.identidade.numero}, 
				e do CPF n&deg; ${estagioMBean.obj.concedente.responsavel.pessoa.cpfCnpjFormatado}, 
				com a interveni�ncia obrigat�ria da ${ configSistema['nomeInstituicao'] }, doravante denominada 
				${ configSistema['siglaInstituicao'] }, neste ato representada pelo Coordenador do Curso de 
				${estagioMBean.obj.discente.curso.descricao}, Prof.(a) ${estagioMBean.coordenador.pessoa.nome}, 
				RG n&deg; ${estagioMBean.coordenador.pessoa.identidade}, CPF n&deg; ${estagioMBean.coordenador.pessoa.cpfCnpjFormatado}, 
				e em conformidade com o que determina a Lei n&ordm; 11.788, de 25 de 
				setembro de 2008, a Resolu��o n&ordm; 178-CONSEPE, de 22 de setembro de 1992, 
				a Resolu��o n&ordm; 227/2009 - CONSEPE, de 03 de dezembro de 2009,
				resolvem firmar o presente Termo, mediante as seguintes cl�usulas e condi��es:
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CL�USULA PRIMEIRA -</b> 			
				O Est�gio possibilitar� ao ESTAGI�RIO o desenvolvimento de atividades pr�ticas relacionadas � 
				sua �rea de forma��o acad�mica, constituindo-se componente indispens�vel para a integraliza��o curricular.			
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CL�USULA SEGUNDA -</b> O Est�gio se realizar� no(a) 
				${estagioMBean.obj.concedente.pessoa.nome}, situado na ${estagioMBean.obj.concedente.pessoa.enderecoContato.descricao}, 
				${estagioMBean.obj.concedente.pessoa.enderecoContato.municipio.nomeUF}, no per�odo de 
				<ufrn:format type="data" valor="${estagioMBean.obj.dataInicio}"/> a 
				<ufrn:format type="data" valor="${estagioMBean.obj.dataFim}"/> 
				correspondendo ao cumprimento da carga hor�ria, no total de ${estagioMBean.duracaoEstagio}
				horas/aula.
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px; margin-bottom: 0cm">
				<b>SUBCL�USULA PRIMEIRA -</b> Na modalidade de Est�gio Curricular Obrigat�rio,
				o total de horas ser� estabelecido pela disciplina de est�gio.
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px; margin-bottom: 0cm">
				<b>SUBCL�USULA SEGUNDA -</b> Na modalidade de Est�gio Curricular N�o Obrigat�rio,
				o est�gio ter� per�odo m�nimo de 06 (seis) meses e m�ximo de 02 (dois) anos.
			</p>
			<br/>				
			<p style="text-align: justify;">
				<b>CL�USULA TERCEIRA -</b> A jornada de atividade do ESTAGI�RIO ser� 
				de at� ${estagioMBean.obj.cargaHorariaSemanal / 5} horas di�rias e 
				at� ${estagioMBean.obj.cargaHorariaSemanal} horas semanais, 
				a ser cumprida de segunda a sexta-feira, das <ufrn:format type="hora" valor="${estagioMBean.obj.horaInicio}"/>
				 �s <ufrn:format type="hora" valor="${estagioMBean.obj.horaFim}"/> horas, sendo vedado o regime de hora extraordin�ria, 
				bem como a realiza��o do est�gio aos domingos e feriados.
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px; margin-bottom: 0cm">
				<b>SUBCL�USULA PRIMEIRA -</b> Em nenhuma hip�tese o est�gio poder� 
				ser realizado concomitantemente com o hor�rio escolar, n�o podendo coincidir com 
				este no todo ou em parte.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CL�USULA QUARTA - </b>Durante o est�gio, O ESTAGI�RIO realizar� as
				atividades previamente planejadas de acordo com o Plano de Atividades, 
				constante na CL�USULA D�CIMA deste termo,
				 sob a orienta��o do Professor ${estagioMBean.obj.orientador.pessoa.nome}, da ${ configSistema['siglaInstituicao'] }
				 e sob a supervis�o do(a) Sr(a). ${estagioMBean.obj.supervisor.nome}, da Concedente.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CL�USULA QUINTA - </b>
				 Durante a realiza��o do Est�gio, o ESTAGI�RIO estar� amparado contra acidentes pessoais, 
				 atrav�s da Ap�lice de Seguro n&ordm; ${estagioMBean.obj.apoliceSeguro} da ${estagioMBean.obj.seguradora}, 
				 CNPJ / MF n&ordm; ${estagioMBean.obj.cnpjSeguradoraFormatado}, no valor de 
				 <ufrn:format type="valor" valor="${estagioMBean.obj.valorSeguro}"/> 
				(<ufrn:format type="extenso" valor="${estagioMBean.obj.valorSeguro}"/>), 
				 sob a responsabilidade da ${ configSistema['siglaInstituicao'] }, quando 
				 se tratar de Est�gio Curricular Obrigat�rio e responsabilidade da CONCEDENTE,
				 quando se tratar de Est�gio Curricular N�o Obrigat�rio.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CL�USULA SEXTA - </b>A realiza��o do est�gio n�o acarretar�
				 por parte do estudante, v�nculo empregat�cio de qualquer natureza, desde que respeitado o 
				&sect;2&ordm; do Art. 3&ordm; da Lei 11.788/08.
			</p>
			<br/>
			<p style="text-align: justify;">
				<b>CL�USULA S�TIMA - </b>O ESTAGI�RIO se compromete a:
			</p>
			<ol type="a">
			    <li>
			    	<p>Realizar, com responsabilidade e esmero, as atividades que lhe forem atribu�das;</p>
			    </li>
			    <li>
			    	<p>Zelar pelos 	materiais, equipamentos e bens em geral do(a) CONCEDENTE, que 	estejam sob os seus cuidados;</p>
			    </li>
			    <li>
			    	<p>Conhecer e cumprir 	os regulamentos e normas internas do Concedente, especialmente 	aquelas 
				que resguardem a manuten��o do sigilo das informa��es a 	
				que tiver acesso em decorr�ncia do est�gio;
				</p>
			    </li>
			    <li>
			    	<p>Apresentar ao Concedente e � ${ configSistema['siglaInstituicao'] } relat�rios semestrais sobre o 
				desenvolvimento	das atividades realizadas;
				</p>
			    </li>
			    <li>
			    	<p>Manter conduta disciplinar de acordo com a moral e os bons costumes;</p>
			    </li>
			    <li>
			    	<p>Comunicar ao Concedente e � ${ configSistema['siglaInstituicao'] }, quando houver conclus�o 
				ou interrup��o do 	curso;
				</p>
			    </li>
			    <li>
			   	<p>Responder pelas 	perdas e danos consequentes da inobserv�ncia das normas e
				condi��es estabelecidas neste Termo.
				</p>
			    </li>
			</ol>
			<br/>
			<p>
				<b>CL�USULA OITAVA - </b>O ESTAGI�RIO ser� desligado do est�gio:
			</p>
			<ol type="a">
			    <li>
			    	<p>Automaticamente, quando do t�rmino do Est�gio;</p>
			    </li>
			    <li>
			    	<p>A qualquer tempo, no interesse ou conveni�ncia do CONCEDENTE e/ou da ${ configSistema['siglaInstituicao'] };</p>
			    </li>
			    <li>
			    	<p>A seu pedido;</p>
			    </li>
			    <li>
			    	<p>Por descumprimento de cl�usula do Termo de Compromisso;</p>
			    </li>
			    <li>
			   	<p>Quando houver conclus�o ou interrup��o do curso.</p>
			    </li>
			    <li>
			   	<p>Depois de decorrida a ter�a parte do tempo previsto para a dura��o
			   	do est�gio, se comprovada a insufici�ncia na avalia��o de desempenho
			   	no �rg�o ou entidade ou na institui��o de ensino.</p>
			    </li>		    
			    <li>
			     <p>Pelo n�o comparecimento, sem motivo justificado, por mais de cinco
			     dias, consecutivos ou n�o, no per�odo de um m�s, ou por trinta dias
			     durante todo o per�odo do est�gio.</p>
			    </li>
			</ol>
			<br/>
			<p>
				<b>CL�USULA NONA - </b>Da Bolsa e Aux�lio Transporte
			</p>		
			<br/>
			<p style="text-align: justify;">
				Quando se tratar de Est�gio curricular N�o Obrigat�rio ou Est�gio Curricular
				Obrigat�rio Remunerado, o ESTAGI�RIO receber� bolsa mensal no valor de 						 
				R$ <ufrn:format type="valor" valor="${estagioMBean.obj.valorBolsa}"/> 
				(<ufrn:format type="extenso" valor="${estagioMBean.obj.valorBolsa}"/>), 
				e Aux�lio Transporte di�rio, no valor de  
				R$ <ufrn:format type="valor" valor="${estagioMBean.obj.valorAuxTransporte}"/> 
				(<ufrn:format type="extenso" valor="${estagioMBean.obj.valorAuxTransporte}"/>)  ao dia, 
				devendo respeitar o disposto na Orienta��o Normativa n&ordm; 07, de 30 de outubro de 2008,
				do Minist�rio de Estado do Planejamento, Or�amento e Gest�o, quando o CONCEDENTE
				for �rg�o Federal.
			</p>
			<br/>
			<p>
				<b>CL�USULA D�CIMA - </b>O ESTAGI�RIO realizar� as seguintes atividades:
			</p>
			<br/>
			<p style="text-align: justify; text-indent: 25px;">
				${estagioMBean.obj.descricaoAtividades}
			</p>
			<br/>						
			<p>E por estarem assim justos e acordados, firmam o presente Termo de Compromisso.</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" valor="${estagioMBean.obj.dataAprovacao != null ? estagioMBean.obj.dataAprovacao : estagioMBean.obj.dataCadastro}"  />.
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.discente.nome}<br/>
				ESTAGI�RIO			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.concedente.pessoa.nome}<br/>
				CONCEDENTE			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.supervisor.nome}<br/>
				SUPERVISOR DE CAMPO			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.coordenador.pessoa.nome}<br/>
				COORDENADOR DO CURSO			
			</p>
			<br/><br/>
			<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
				__________________________________<br/>
				${estagioMBean.obj.orientador.pessoa.nome}<br/>
				ORIENTADOR			
			</p>
			<br/><br/>
		</div>
		<style>
			#divAutenticacao {
				width: 97%;
				margin: 10px auto 2px;
				text-align: center;
			}
			
			#divAutenticacao h4 {
				border-bottom: 1px solid #BBB;
				margin-bottom: 3px;
				padding-bottom: 2px;
			}
			
			#divAutenticacao span {
				color: #922;
				font-weight: bold;
			}	
		</style>
		
		<div id="divAutenticacao">
			<h4>ATEN��O</h4>
			<p>
				Para verificar a autenticidade deste documento acesse
				<span>${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando o identificador <i>(${estagioMBean.identificadorDocumento})</i>, a data de emiss�o e
				o c�digo de verifica��o <span>${estagioMBean.codigoSeguranca}</span>
			</p>
		</div>				
	</c:if>
	<c:if test="${!requestScope.liberaEmissao}"> <%-- seguran�a importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>
			<style type="text/css">
				
				#div1{
					font-weight: bold;
					color: red;
					width: 100%;
					text-align: center;
				}
				
				#div2{
					margin-top:  20px;  
					font-style: italic;
					width: 100%;
					text-align: center;
				}
			
			</style>			
				
			<div style="margin-bottom:30px;">	
				<div id="div1"> ERRO NA GERA��O DO TERMO DE RESCIS�O DE EST�GIO CURRICULAR SUPERVISIONADO.</div>
		
				<div id="div2"> As informa��es do documento s�o inv�lidas </div>
			</div>	
	</c:if>		

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>	