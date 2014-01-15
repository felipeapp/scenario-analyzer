<%@include file="/public/include/cabecalho.jsp" %>

<style>
	.colInscricao{width: 80px;text-align: right !important;}
	.colData{width: 100px;text-align: center !important;}
</style>

<f:view>
<%-- 
	<h:outputText value="#{util.create}"/>
--%>

	<h2> Inscrições realizadas em Processos Seletivos - ${processoSeletivo.nivelDescricao} </h2>


	<c:if test="${not empty inscricaoSelecao.resultadosBusca}">
		<div class="descricaoOperacao">
			<p>
				Abaixo estão listadas todas as incrições em processos seletivos encontradas
				para o 
				<h:outputText value="CPF" rendered="#{!inscricaoSelecao.obj.pessoaInscricao.estrangeiro}"/>
				<h:outputText value="Passaporte" rendered="#{inscricaoSelecao.obj.pessoaInscricao.estrangeiro}"/>
				 informado, permitindo que seja reemitido o comprovante de inscrição.
			</p>
		</div>
		
		<c:forEach items="#{inscricaoSelecao.resultadosBusca}" var="item" varStatus="status" end="0">
			<c:set var="temGRU" value="#{item.processoSeletivo.editalProcessoSeletivo.taxaInscricao > 0}" />
		</c:forEach>
		<h:form>
			<div class="infoAltRem" style="width:90%; text-align: center">
				<h:graphicImage value="/img/comprovante.png" style="vertical-align: middle; overflow: visible;" />: Visualizar Comprovante
				<c:if test="${temGRU}" >
					<h:graphicImage value="/img/imprimir.gif" style="vertical-align: middle; overflow: visible;" />: Imprimir a GRU da Taxa de Inscrição
				</c:if>
			</div>
			<br>
			<table class="listagem">
				<caption class="listagem">
					Inscrições para o 
					<c:choose>
						<c:when test="${inscricaoSelecao.obj.pessoaInscricao.estrangeiro}">
							Passaporte  <b>${inscricaoSelecao.obj.pessoaInscricao.passaporte}</b>
						</c:when>
						<c:otherwise>
							CPF  <b><ufrn:format type="cpf_cnpj" name="inscricaoSelecao" property="obj.pessoaInscricao.cpf"/></b>
						</c:otherwise>
					</c:choose>
				</caption>
				<thead>
					<tr>
						<th class="colInscricao"><strong>No. Inscrição</strong></th>
						<th><strong>Nome</strong></th>
						<th><strong>Processo Seletivo</strong></th>
						<th><strong>Curso</strong></th>
						<th class="colData"><strong>Data de Inscrição</strong></th>
						<th><strong>Status</strong></th>
						<%-- SE PROCESSO SELETIVO TRANSFERÊNCIA VOLUNTÁRIA --%>
						<th></th>
						<th colspan="2"></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{inscricaoSelecao.resultadosBusca}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td class="colInscricao">${item.numeroInscricao}</td>
						<td>${item.pessoaInscricao.nome}</td>
						<c:choose>
							<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
							<c:when test="${not empty item.processoSeletivo.curso}">
								<td>${item.processoSeletivo.editalProcessoSeletivo.nome}</td>
								<td>${item.processoSeletivo.nome}</td>
							</c:when>
							<%-- SE PROCESSO SELETIVO GRADUAÇÃO --%>
							<c:otherwise>
								<td>${item.processoSeletivo.editalProcessoSeletivo.nome}</td>
								<td>${item.processoSeletivo.matrizCurricular.curso.descricao}</td>
							</c:otherwise>
						</c:choose>	
						<td class="colData"><ufrn:format type="data" valor="${item.dataInscricao}" /></td>
						<td width="35">	${item.descricaoStatus}</td>
						<td width="16" align="right">
							<h:commandLink title="Visualizar Comprovante" action="#{inscricaoSelecao.verComprovante}" id="visualizarComprovante">
								<h:graphicImage url="/img/comprovante.png"/>
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
						<td width="16" align="right">
						<h:commandLink action="#{inscricaoSelecao.imprimirGRU}" rendered="#{item.processoSeletivo.editalProcessoSeletivo.taxaInscricao > 0}" id="imprimirGRU">
							<h:graphicImage url="/img/imprimir.gif" alt="Imprimir a GRU da Taxa de Inscrição"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</h:form>
	</c:if>
	<c:if test="${empty inscricaoSelecao.resultadosBusca}">
		Não há inscrições cadastradas para o CPF informado.
	</c:if>
</f:view>

<br />
<div class="voltar" style="text-align: center;">
	<a href="javascript: history.go(-1);"> << Voltar </a>
</div>

<%@include file="/public/include/rodape.jsp" %>