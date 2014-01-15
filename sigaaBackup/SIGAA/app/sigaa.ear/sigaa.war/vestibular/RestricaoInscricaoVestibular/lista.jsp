<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Listar / Alterar Restrições de Inscrição no Vestibular</h2>
	
	<h:form id="form">
		<a4j:keepAlive beanName="restricaoInscricaoVestibular"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Informe os Parâmetros</caption>
			<tr>
				<th>Processo Seletivo:</th>
				<td>
					<h:selectOneMenu id="processoSeletivo" onchange="submit()" 
						value="#{restricaoInscricaoVestibular.obj.processoSeletivoVestibular.id}"
						valueChangeListener="#{restricaoInscricaoVestibular.processoSeletivoListener}">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Matriz Curricular:</th>
				<td>
					<h:selectOneMenu id="primeiraOpcao" 
						value="#{restricaoInscricaoVestibular.obj.matrizCurricular.id}">
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{restricaoInscricaoVestibular.listaMatrizOfertaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{restricaoInscricaoVestibular.buscar}" id="cadastrar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{restricaoInscricaoVestibular.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${not empty restricaoInscricaoVestibular.resultadosBusca}">
			<div class="infoAltRem"  style="width: 100%;">
				<h:graphicImage value="/img/alterar.gif"	style="overflow: visible;" />: Alterar Restrição
				<h:graphicImage value="/img/delete.gif"	style="overflow: visible;" />: Remover Restrição
			</div>
			<table class="listagem">
				<caption>Resultados da Busca</caption>
				<thead>
					<tr>
						<th>Descrição</th>
						<th>Tipo de Restrição</th>
						<th>Matriz Curricular</th>
						<th></th>
						<th></th>
					</tr>
					<tr>
						<th colspan="5">Lista de CPFs</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{restricaoInscricaoVestibular.resultadosBusca}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.descricao}</td>
						<td>${item.descricaoTipoRestricao}</td>
						<td>${item.matrizCurricular.descricao}</td>
						<td width="2%" rowspan="2">
							<h:commandLink title="Alterar" 
								action="#{restricaoInscricaoVestibular.atualizar}" style="border: 0;">
								<f:param name="id" value="#{item.id}" />
								<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</td>
						<td width="2%" rowspan="2">
							<h:commandLink title="Remover" 
								action="#{restricaoInscricaoVestibular.preRemover}" style="border: 0;">
								<f:param name="id" value="#{item.id}" />
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</td>
					</tr>
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td colspan="3">
							<c:forEach items="#{item.cpfs}" var="cpf" varStatus="statusCPF">
								<ufrn:format type="cpf_cnpj" valor="${cpf}"/>
								<c:if test="${statusCPF.index + 1 < fn:length(item.cpfs)}">, </c:if>
							</c:forEach>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>