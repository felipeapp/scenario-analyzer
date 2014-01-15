<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<c:if test="${!acesso.ppg}">
<%@include file="/stricto/menu_coordenador.jsp" %>
</c:if>
<h2 class="title"><ufrn:subSistema /> > Gerenciar Orienta��es</h2>

<div class="descricaoOperacao">
<p>Caro Usu�rio(a),</p><br/>

<p>
	Atrav�s desta opera��o ser� poss�vel gerenciar - cadastrar, finalizar, alterar e cancelar - as orienta��es dos alunos. 
</p>
 
<p> Conv�m, antes de iniciar o gerenciamento das orienta��es, ler atentamente as observa��es abaixo: </p>

<ul>
	<li> Cada discente pode ter apenas um orientador e v�rios co-orientadores.</li>
	 
	<li> A orienta��o de um discente � registrada automaticamente no m�dulo de registro de produ��es 
	intelectuais e ser� contabilizada nos relat�rios de produtividade do orientador. Para tal, � considerado 
	o per�odo entre o in�cio e o fim da orienta��o.
	</li>
	
	<li>
	A opera��o <b>finalizar orienta��o</b> demarca o fim do per�odo da orienta��o que ser� considerado 
	para a contabiliza��o do relat�rio de produtividade.	
	</li>   
	
	<li>
		A opera��o <b>cancelar orienta��o</b> remove todos os registros da orienta��o, incluindo aqueles registradoss 
		no relat�rio de produtividade. Esta opera��o dever� ser utilizada <b>SOMENTE</b> nos casos da orienta��o ter sido cadastrada errada.
		A produtividade lan�ada no relat�rio do m�dulo de produ��o intelectual ser� <b>CANCELADA</b>.  
	</li>
</ul>

<p>	Em caso de d�vidas, contate seu orientador para maiores esclarecimentos. </p>
</div>

<c:set value="#{orientacaoAcademica.discenteBusca}" var="discente" />
<%@ include file="/graduacao/info_discente.jsp"%>

<h:form prependId="false">
<center>
<div class="infoAltRem">
	<h:commandLink value="" action="#{orientacaoAcademica.iniciarCadastro}" id="adicionar" >
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	</h:commandLink>
	<h:commandLink value="Cadastrar Nova Orienta��o" action="#{orientacaoAcademica.iniciarCadastro}" id="cadOrientador"></h:commandLink>
	<br>
	<img src="/sigaa/img/alterar.gif">: Alterar Dados da Orienta��o
	<img src="/sigaa/img/cronograma/remover.gif">: Finalizar Orienta��o
	<img src="/sigaa/img/delete.gif">: Cancelar Orienta��o
</div><br>
</center>

<c:if test="${discente.concluido}">
	<div class="descricaoOperacao">
		<p><b>ATEN��O!</b></p>
		<p>Para os alunos <b>CONCLU�NTES</b>, s� ser� permitido gerenciar as orienta��es n�o finalizadas, ou cadastrar caso n�o exista.</p>		
	</div>
</c:if>		

<c:if test="${ empty orientacaoAcademica.orientacoes }">
	<center> 
		<font color="red"> O aluno ${orientacaoAcademica.discenteBusca} n�o possui nenhum orientador ou co-orientador cadastrado. </font>
		<br/><br/>
		<h:commandLink action="#{orientacaoAcademica.iniciar}" value="<< Selecionar Outro Discente" id="selectOutro" /> 
	</center>
 </c:if>
	 
<c:if test="${ not empty orientacaoAcademica.orientacoes }">

	<table class="listagem" >
		<caption>Orientador(es)</caption>
		
		<c:set var="tipoAtual" value="-1" />
		<c:forEach var="orientacao" varStatus="status" items="#{orientacaoAcademica.orientacoes}">
			<c:if test="${orientacao.tipoOrientacao != tipoAtual}">
 			<c:set var="tipoAtual" value="${orientacao.tipoOrientacao}" />
			<c:set var="subTotal" value="0" />
			<tr class="destaque" style="background-color: #C8D5EC; font-weight: bold; padding-left: 20px;">
	 			<td colspan="6" style="font-weight: bold">Tipo de Orienta��o: ${orientacao.tipoOrientacaoString}</td>
	 		</tr>
	 		<tr class="destaque">
	 			<td width="60%" style="font-weight: bold">Docente</td>
	 			<td width="15%" style="font-weight: bold; text-align: center;">Inicio</td>
	 			<td width="15%" style="font-weight: bold; text-align: center;">Fim</td>
	 			<td width="2%"></td>
	 			<td width="2%"></td>
	 			<td width="2%"></td>
	 		</tr>
			</c:if>
		<c:set var="subTotal" value="${subTotal + 1}" />
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td>${orientacao.descricaoOrientador}</td>
				<td style="text-align: center;">
					<ufrn:format type="data" valor="${orientacao.inicio}" />
				</td>
				<td style="text-align: center;">
 					<ufrn:format type="data" valor="${orientacao.fim}" />
				</td>
				<td width="1%">
				<h:commandLink id="atualizar" action="#{orientacaoAcademica.atualizar}" title="Alterar dados da orienta��o" rendered="#{!orientacao.cancelado && orientacao.fim == null}">
					<f:param name="id" value="#{orientacao.id}"/>
					<h:graphicImage value="/img/alterar.gif"/>
				</h:commandLink>
				</td>
				<td width="1%">
				<h:commandLink id="finalizar" action="#{orientacaoAcademica.preRemover}" title="Finalizar Orienta��o"  rendered="#{!orientacao.cancelado && orientacao.fim == null}">
					<f:param name="id" value="#{orientacao.id}"/>
					<h:graphicImage value="/img/cronograma/remover.gif"/>
				</h:commandLink>
				</td>
				<td width="1%">
					<h:commandLink id="cancelar" title="Cancelar Orienta��o" action="#{orientacaoAcademica.cancelarOrientacao}">
						<f:param name="id" value="#{orientacao.id}"/>
						<h:graphicImage value="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		
		<tfoot>
			<td colspan="6" align="center">
				<h:commandButton action="#{orientacaoAcademica.iniciar}" value="<< Selecionar Outro Discente" id="selectOutroDisc" />
			</td>
		</tfoot>
	</table>
	
</c:if>
</h:form>
		
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
		