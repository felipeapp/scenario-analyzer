<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<a4j:keepAlive beanName="trancamentoPrograma" />


<style>
.solicitacao {
	font: 16px arial,sans-serif;
	text-align:justify;
}

.titulo {
	font: 23px arial,sans-serif;
	font-weight:bold;
	text-decoration:underline;
	text-align: center;
	
}

.quadro {
	font: 17px arial,sans-serif;
	font-weight:bold;
	text-align:center;
	border:1px solid black;
	padding: 15px; 
}

.autenticacao {
	color: #922;
	font-weight: bold;
}


h4 {
	border-bottom: 1px solid #BBB;
	margin-bottom: 3px;
	padding-bottom: 2px;
	text-align: center;
}

</style>
<f:view>
	<br/>
  <div align="center"><h:graphicImage value="/img/prograd.gif"/></div>
  <br/><br/>
 <p class="titulo">SOLICITA��O DE TRANCAMENTO DE PROGRAMA</p>
 <br/><br/>
 	<c:if test="${trancamentoPrograma.obj.solicitado}">
		<p style="text-align: center; color: red;">
			<b>A T E N � � O!</b><br/>
			<c:if test="${trancamentoPrograma.obj.discente.graduacao}">
			    O trancamento de programa do per�odo ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo} somente ser� efetivado mediante apresenta��o presencial ao ${ configSistema['siglaUnidadeGestoraGraduacao'] } deste documento.
			</c:if>
			<c:if test="${trancamentoPrograma.obj.discente.stricto}">
				O Trancamento s� ser� efetivado mediante aprova��o da Coordena��o do curso.	
			</c:if>
		</p>
		<br/>	
	</c:if>
	
	<c:set value="#{trancamentoPrograma.discente}" var="d"/>
	<p class="solicitacao">
				
		&emsp;&emsp; Eu,  <span style="font-weight:bold;">${d.pessoa.nome }</span>, matr�cula <span style="font-weight:bold;">${d.matricula }</span>, do curso <span style="font-weight:bold;">
		<c:if test="${d.graduacao}">  ${d.matrizCurricular.descricao}</c:if><c:if test="${not d.graduacao}">  ${d.curso.descricao }</c:if></span>, solicito o trancamento de programa para o per�odo letivo <span style="font-weight:bold;">${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo}</span>, de acordo com o art. 261 da Resolu��o n� 227/2009 - CONSEPE, de 03/12/2009.<br/><br/>
	    &emsp;&emsp; Estou ciente de que essa solicita��o de trancamento s� � v�lida para o per�odo letivo <c:if test="${ not trancamentoPrograma.obj.posteriori }">atual</c:if> <c:if test="${ trancamentoPrograma.obj.posteriori }">${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo}</c:if> e que o limite m�ximo para trancamento de programa � de quatro per�odos letivos regulares, consecutivos ou n�o.<br/><br/>
	</p>    
		<c:if test="${not trancamentoPrograma.obj.posteriori and trancamentoPrograma.obj.solicitado}">
			<p class="solicitacao">
				&emsp;&emsp; Estou ciente tamb�m que, para que o trancamento seja efetivado, devo comparecer � Coordenadoria de Atendimento da Pr�-Reitoria de Gradua��o at� dia 
				<span style="font-weight:bold;">
					<c:if test="${ not trancamentoPrograma.obj.posteriori }"><ufrn:format type="data" valor="${trancamentoPrograma.calendarioVigente.fimTrancamentoPrograma}" /></c:if>
					<c:if test="${ trancamentoPrograma.obj.posteriori }"><ufrn:format type="data" valor="${trancamentoPrograma.calendarioVigente.fimTrancamentoProgramaPosteriori}" /></c:if>
				</span>, no seu hor�rio de atendimento ao p�blico, e apresentar esta solicita��o assinada para que a mesma seja registrada no Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA.
				<br/><br/>
			</p>
			<br/><br/><br/>
	
			<p class="solicitacao" style="text-align: center;font-weight:bold;">  ${ configSistema['cidadeInstituicao'] }, ${trancamentoPrograma.dataSolicitacao }</p>
			<br/>
			<p class="solicitacao" style="text-align: center;">___________________________________</p>
			<br/>
			<p class="solicitacao" style="text-align: center;">Assinatura do Aluno</p><br/>
					
			<div class="quadro">
				<p style="text-decoration:underline;">COMPROVANTE DE REGISTRO DO TRANCAMENTO DE PROGRAMA NO SIGAA</p><br/>
				<p>Servidor Respons�vel: <span style="font-weight: normal;">______________________________ </span>Data:<span style="font-weight: normal;">__/__/__</span> </p>
			</div>
			<br/>
		</c:if>
				
		<h:outputText rendered="#{trancamentoPrograma.obj.posteriori}">
			<div style="height: 40%"></div>
		</h:outputText>
		<div id="autenticacao">
			<h4>ATEN��O</h4>
			<p>
				Para verificar a autenticidade deste documento acesse
				<span class="autenticacao">${ configSistema['linkSigaa'] }/sigaa/documentos/</span> informando a matr�cula, a data de emiss�o e
				o c�digo de verifica��o <span class="autenticacao">${trancamentoPrograma.codigoSeguranca}</span>
				<c:if test="${trancamentoPrograma.posteriorLimiteTrancamento}">
					<br/>Essa solicita��o foi impressa no dia ${trancamentoPrograma.dataAtual }, ap�s o prazo limite de trancamento (${trancamentoPrograma.dataLimiteTrancamento }).
				</c:if>
			</p>
		</div>
		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>