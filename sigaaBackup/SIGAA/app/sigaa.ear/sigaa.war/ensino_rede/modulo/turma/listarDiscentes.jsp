<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<h2><ufrn:subSistema /> &gt; 
		<c:if test="${!turmaRedeMBean.alterar}">	
			Criar Turma
		</c:if>
		<c:if test="${turmaRedeMBean.alterar}">	
			Alterar Turma
		</c:if>
	</h2>
	
	<h:form id="form" prependId="false">
	
	<br/>
	<div class="descricaoOperacao" >
		<b>Caro usuário,</b> 
		<br/><br/>
		Nesta tela é possível selecionar os discentes que serão matriculados na turma.
	</div>	
	
	<c:set var="turma" value="${turmaRedeMBean.obj}" />
	<%@include file="/ensino_rede/modulo/turma/info_turma.jsp"%>
	
		<table class="formulario" width="25%">
		<caption>Busca por Discentes</caption>
		<tbody>
			<tr>
				<td><label for="busca:chkNome">Ano-Período de Ingresso: </label></td>
				<td>			
					<h:inputText value="#{ turmaRedeMBean.anoFiltro }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano" />-
					<h:inputText value="#{ turmaRedeMBean.periodoFiltro }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
	
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{turmaRedeMBean.buscarDiscentes}" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br/>

	<table class="formulario" style="width: 90%">
		<caption> Informe os discentes que gostaria de matricular</caption>
		<c:if test="${ not empty turmaRedeMBean.discentes}">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="checkAll()" title="Selecionar Todos"/></th>
				<th width="70%">Discente</th>
				<th style="text-align:center">Período de Ingresso</th>
				<th style="text-align:center">Situação</th>				
			</tr>
		</thead>		
		<tbody>
				<c:forEach items="#{turmaRedeMBean.discentes}" var="d" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td width="1%"> 
							<input type="checkbox" name="selecionados" id="check_${d.id}" value="${d.id}" class="check"/>
						</td> 
						<td>${d.nome}</td>
						<td style="text-align:center">${d.anoIngresso}.${d.periodoIngresso}</td>
						<td style="text-align:center">${d.status.descricao}</td>
					</tr>			
				</c:forEach>
		</tbody>
		</c:if>
		<c:if test="${ empty turmaRedeMBean.discentes}">
			<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="4" align="center" width="1%"> 
					<div style="color:red;font-weight:bold;">Não existem discentes passíveis de matrícula na turma.</div>
				</td> 
			</tr>			
		</c:if>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Adicionar Discentes" action="#{turmaRedeMBean.adicionarDiscentes}" id="addDiscente" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	
		<div class="infoAltRem" style="width:90%">
			<img src="${ctx}/img/delete.gif"/>: Remover Discente
		</div>

		<table class="formulario" style="width: 90%">
			<caption>Discentes Escolhidos</caption>
			<c:if test="${ not empty turmaRedeMBean.discentesEscolhidos}">
			<thead>
				<tr>
					<th width="70%">Discente</th>
					<th style="text-align:center">Período de Ingresso</th>
					<th style="text-align:center">Situação</th>		
					<th  width="1%"></th>		
				</tr>
			</thead>		
			<tbody>
					<c:forEach items="#{turmaRedeMBean.discentesEscolhidos}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${d.nome}</td>
							<td style="text-align:center">${d.anoIngresso}.${d.periodoIngresso}</td>
							<td style="text-align:center">${d.status.descricao}</td>
							<td>
								<h:commandLink title="Remover Discente" style="border: 0;" action="#{turmaRedeMBean.removerDiscente}" onclick="#{ confirmDelete }">
									<f:param name="idDiscente" value="#{d.id}" />
									<h:graphicImage url="/img/delete.gif" alt="Excluir" />
								</h:commandLink>
							</td>
						</tr>			
					</c:forEach>
			</tbody>
			</c:if>
			<c:if test="${ empty turmaRedeMBean.discentesEscolhidos}">
				<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td colspan="4" align="center" width="1%"> 
						<div style="color:red;font-weight:bold;">Nenhum discente adicionado na turma.</div>
					</td> 
				</tr>	
			</c:if>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="<< Selecionar Campus" action="#{turmaRedeMBean.telaSelecaoCampus}" id="voltarTelaCampus" rendered="#{!turmaRedeMBean.alterar}"/>
						<h:commandButton value="<< Selecionar Componente" action="#{turmaRedeMBean.telaSelecaoComponentes}" id="outra2" rendered="#{!turmaRedeMBean.alterar}"/>
						<h:commandButton value="<< Selecionar Turma" action="#{turmaRedeMBean.telaBuscarTurmas}" id="turmas2" rendered="#{turmaRedeMBean.alterar}"/>
						<h:commandButton value="<< Selecionar Dados Gerais" action="#{turmaRedeMBean.telaDadosGerais}" id="dadosGerais2"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{turmaRedeMBean.cancelar}" id="cancelar2" />
						<h:commandButton value="Próximo Passo >>" action="#{turmaRedeMBean.submeterDiscentes}" id="selecionar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
	</h:form>

</f:view>

<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>
<script type="text/javascript">
function checkAll() {
	marcar = !marcar;
	var checkList = J(document.getElementsByClassName('check'))[0];
    for (i=0; i<checkList.length; i++){
		var e = checkList[i];
		if (!marcar)			
			e.checked = false;
		else
			e.checked = true;
	}
}
var marcar = false;
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>