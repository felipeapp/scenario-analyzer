<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Cadastro �nico de Bolsistas > Apresenta��o</h2>

<f:view>

<div class="descricaoOperacao">
	<p>
		<center><strong>Programa de Bolsas de Assist�ncia Estudantil</strong></center>
	</p>		
	
	<br/>
	
	<p>
		O programa de bolsa de assist�ncia estudantil � concedido a alunos de cursos regulares, 
		com prioridade para aqueles que se enquadrarem na condi��o de aluno s�cio-economicamente carente.
	</p>
	
	<br/>
	
	<p>
		Entendem-se como cursos regulares aqueles oferecidos por qualquer unidade de ensino vinculada � ${ configSistema['siglaInstituicao'] }, 
		nos n�veis m�dio, t�cnico profissionalizante ou equivalentes, gradua��o (presencial ou a dist�ncia) e p�s-gradua��o strictu sensu.
	</p>

	<br/>

	<p>
		<strong>Mais detalhes podem ser encontrados na Resolu��o no <a href="http://www.sigrh.ufrn.br/sigrh/downloadArquivo?idArquivo=78385&key=1487be07add014640fa59dd488710b77" targer="_blank">169/2008-CONSEPE.</a></strong>
	</p>
	<br />
	<br />
	<p>
		<center><strong>Question�rio S�cio-Econ�mico</strong></center>
	</p>		
	
	<br/>

	<p>
		O question�rio � uma das formas de avalia��o para determinar a condi��o s�cio econ�mica do aluno. 
		Essa etapa � obrigat�ria a todos os discentes que desejam participar do programa de bolsa.	
	</p>

	<br/>

	<p>
		A veracidade dos dados informados no cadastro � de sua responsabilidade. 
		Constatando-se que os dados informados s�o falsos, voc� poder� sofrer medidas administrativas cab�veis que incluem a perda da bolsa.
	</p>				
</div>
	
	<h:messages showDetail="true"></h:messages>
	
	<h:form id="form">
		<table width="100%">
			<tbody align="center">
				<tr>
					<td>
						<h:selectBooleanCheckbox id="checkConcorda" value="#{adesaoCadastroUnico.termoConcordancia}" /> 
						<label for="form:checkConcorda">
							Eu li e concordo os termos acima citados.
						</label>
					</td>
				</tr>
				<tr>
					<td>
						<h:commandButton value="Continuar >>" action="#{adesaoCadastroUnico.iniciarPerfil}" />
					</td>
				</tr>
			</tbody>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>