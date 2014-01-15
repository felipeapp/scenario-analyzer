<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Autorização de Propostas de Ações Acadêmicas</h2>

	<div class="descricaoOperacao">
		Caro docente,<br />
		somente as propostas que ainda não foram autorizadas por nenhum dos departamentos envolvidos na ação podem ser devolvidas para reedição
		pelo(a) coordenador(a).			
	</div>


	<h:outputText value="#{autorizacaoDepartamento.create}"/>
	<h:outputText value="#{atividadeExtensao.create}"/>	

	<div class="infoAltRem">
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="leg_veiw"/>: Visualizar proposta
	    <h:graphicImage value="/img/extensao/printer.png" style="overflow: visible;" id="leg_recibo"/>: Re-imprimir recibo
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="leg_seta"/>: Analisar proposta
	    <br />
	    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" id="leg_devolver"/>: Devolver proposta para Coordenador(a)
	</div>

<c:set value="${autorizacaoDepartamento.autorizacoesDepartamentoUsuarioLogado}" var="autorizacoes"/>

<h:form id="form">
	<table class="listagem">
	<caption>Lista de todas as Ações Acadêmicas aguardando sua Autorização (${fn:length(autorizacoes)})</caption>
    <thead>
		<tr>
			<th>Ano </th>
			<th>Título </th>
			<th>Analisado Em </th>
			<th>Autorizado </th>
			<th></th>			
			<th></th>			
			<th></th>			
			<th></th>
		</tr>
	</thead>
	
	
	<c:if test="${not empty autorizacaoDepartamento.autorizacoes}">
		<c:forEach items="#{autorizacaoDepartamento.autorizacoes}" var="auto" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">			
				<td> ${auto.atividade.ano} </td>
				<td width="55%"> ${auto.atividade.titulo} </td>
				<td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${auto.dataAutorizacao}" /> <c:if test="${empty auto.dataAutorizacao}"> <font color="red">NÃO ANALISADO</font></c:if> </td>
				<td>
					 <c:if test="${not empty auto.dataAutorizacao}"> ${auto.autorizado ? 'SIM':'NÃO'} </c:if>
				</td>	
				<td>				
					<h:commandLink title="Visualizar Proposta" action="#{atividadeExtensao.view}" style="border: 0;" id="view_acao_">
						<f:param name="id" value="#{auto.atividade.id}"/>
		               	<h:graphicImage url="/img/view.gif"/>
					</h:commandLink>
				</td>	
				<td>								               
					<h:commandLink action="#{autorizacaoDepartamento.reciboAcaoExtensao}" style="border: 0;" id="reciboAcaoExtensao" rendered="#{(not empty auto.dataAutorizacao)}">
					   <f:param name="id_autorizacao" value="#{auto.id}"/>					   
				       <h:graphicImage url="/img/extensao/printer.png" id="img_recibo_"/>
					</h:commandLink>
				</td>
				<td>	
					<h:commandLink title="Analisar Proposta" action="#{autorizacaoDepartamento.escolherAutorizacao}" 
						style="border: 0;"  id="selecionar_autorizacao_" rendered="#{(empty auto.dataAutorizacao)}">
					        <f:param name="id_autorizacao" value="#{auto.id}"/>
	                		<h:graphicImage url="/img/seta.gif"/>
					</h:commandLink>
				</td>
				<td>
					<h:commandLink title="Devolver Proposta para Coordenador(a)" action="#{atividadeExtensao.reeditarProposta}"	 
						style="border: 0;"
						rendered="#{(empty auto.dataAutorizacao)}"
						onclick="return confirm('Tem certeza que deseja Devolver esta proposta para Coordenador(a)?');" id="devolver_coord_dpto_">
					         <f:param name="id" value="#{auto.atividade.id}"/>
                   			<h:graphicImage url="/img/arrow_undo.png"/>
					</h:commandLink>
				</td>				
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${empty autorizacaoDepartamento.autorizacoes}">            
			<tr>
				<td colspan="4"><center><font color="red">O usuário atual não possui Ações pendentes de Autorização<br/></font> </center></td>
			</tr>
	</c:if>
			
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>