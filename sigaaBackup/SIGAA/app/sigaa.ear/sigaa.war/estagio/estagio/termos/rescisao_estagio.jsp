<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
	<style>
		#textoConteudo {
			line-height: 150%;
			font-family: serif;
			font-size: 12pt;
		}
	</style>    
	<div id="textoConteudo">	
		<h2>		
			TERMO DE RESCIS�O DE EST�GIO CURRICULAR SUPERVISIONADO
		</h2>	
		<c:if test="${requestScope.liberaEmissao == true}"> <%-- seguran�a importante senao o usuario pode acessar a pagina diretamente e ver o documento --%>
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
					resolvem Rescindir o presente Termo de Compromisso de Est�gio, 
					mediante as seguintes cl�usulas e condi��es:
				</p>
				<br/>
				<p style="text-align: justify;">
					<b>CL�USULA PRIMEIRA -</b>
					O Est�gio iniciado em <ufrn:format type="data" valor="${estagioMBean.obj.dataInicio}"/>
					ser� finalizado em <ufrn:format type="data" valor="${estagioMBean.obj.dataCancelamento}"/>
					<c:if test="${not empty estagioMBean.obj.interessadoQueCancelou}">
						, por iniciativa do ${estagioMBean.obj.interessadoQueCancelou}
					</c:if>.
				</p>
				<br/>						
				<p>E por estarem assim justos e acordados, firmam o presente Termo de Compromisso.</p>
				<br/><br/>
				<p align="center" style="margin-left: 0.5cm; margin-bottom: 0cm">
					${ configSistema['cidadeInstituicao'] }, <ufrn:format type="dia_mes_ano" valor="${estagioMBean.obj.dataCancelamento}"  />.
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
		</c:if>	
	</div>
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