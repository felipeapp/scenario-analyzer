<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<c:if test="${!acesso.ppg}">
<%@include file="/stricto/menu_coordenador.jsp" %>
</c:if>
<h2 class="title"><ufrn:subSistema /> > Gerenciar Orientações</h2>

<div class="descricaoOperacao">
<p>Caro Usuário(a),</p><br/>

<p>
	Através desta operação será possível gerenciar - cadastrar, finalizar, alterar e cancelar - as orientações dos alunos. 
</p>
 
<p> Convém, antes de iniciar o gerenciamento das orientações, ler atentamente as observações abaixo: </p>

<ul>
	<li> Cada discente pode ter apenas um orientador e vários co-orientadores.</li>
	 
	<li> A orientação de um discente é registrada automaticamente no módulo de registro de produções 
	intelectuais e será contabilizada nos relatórios de produtividade do orientador. Para tal, é considerado 
	o período entre o início e o fim da orientação.
	</li>
	
	<li>
	A operação <b>finalizar orientação</b> demarca o fim do período da orientação que será considerado 
	para a contabilização do relatório de produtividade.	
	</li>   
	
	<li>
		A operação <b>cancelar orientação</b> remove todos os registros da orientação, incluindo aqueles registradoss 
		no relatório de produtividade. Esta operação deverá ser utilizada <b>SOMENTE</b> nos casos da orientação ter sido cadastrada errada.
		A produtividade lançada no relatório do módulo de produção intelectual será <b>CANCELADA</b>.  
	</li>
</ul>

<p>	Em caso de dúvidas, contate seu orientador para maiores esclarecimentos. </p>
</div>

<c:set value="#{orientacaoAcademica.discenteBusca}" var="discente" />
<%@ include file="/graduacao/info_discente.jsp"%>

<h:form prependId="false">
<center>
<div class="infoAltRem">
	<h:commandLink value="" action="#{orientacaoAcademica.iniciarCadastro}" id="adicionar" >
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	</h:commandLink>
	<h:commandLink value="Cadastrar Nova Orientação" action="#{orientacaoAcademica.iniciarCadastro}" id="cadOrientador"></h:commandLink>
	<br>
	<img src="/sigaa/img/alterar.gif">: Alterar Dados da Orientação
	<img src="/sigaa/img/cronograma/remover.gif">: Finalizar Orientação
	<img src="/sigaa/img/delete.gif">: Cancelar Orientação
</div><br>
</center>

<c:if test="${discente.concluido}">
	<div class="descricaoOperacao">
		<p><b>ATENÇÃO!</b></p>
		<p>Para os alunos <b>CONCLUÍNTES</b>, só será permitido gerenciar as orientações não finalizadas, ou cadastrar caso não exista.</p>		
	</div>
</c:if>		

<c:if test="${ empty orientacaoAcademica.orientacoes }">
	<center> 
		<font color="red"> O aluno ${orientacaoAcademica.discenteBusca} não possui nenhum orientador ou co-orientador cadastrado. </font>
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
	 			<td colspan="6" style="font-weight: bold">Tipo de Orientação: ${orientacao.tipoOrientacaoString}</td>
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
				<h:commandLink id="atualizar" action="#{orientacaoAcademica.atualizar}" title="Alterar dados da orientação" rendered="#{!orientacao.cancelado && orientacao.fim == null}">
					<f:param name="id" value="#{orientacao.id}"/>
					<h:graphicImage value="/img/alterar.gif"/>
				</h:commandLink>
				</td>
				<td width="1%">
				<h:commandLink id="finalizar" action="#{orientacaoAcademica.preRemover}" title="Finalizar Orientação"  rendered="#{!orientacao.cancelado && orientacao.fim == null}">
					<f:param name="id" value="#{orientacao.id}"/>
					<h:graphicImage value="/img/cronograma/remover.gif"/>
				</h:commandLink>
				</td>
				<td width="1%">
					<h:commandLink id="cancelar" title="Cancelar Orientação" action="#{orientacaoAcademica.cancelarOrientacao}">
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
		