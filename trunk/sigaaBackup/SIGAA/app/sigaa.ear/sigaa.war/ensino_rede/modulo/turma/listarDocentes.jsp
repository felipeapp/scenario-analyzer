<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
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
	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		Nesta tela é possível selecionar os docentes que lecionarão na turma.
	</div>	
	
	<c:set var="turma" value="${turmaRedeMBean.obj}" />
	<%@include file="/ensino_rede/modulo/turma/info_turma.jsp"%>
	
		<table class="formulario" style="width: 90%">
		<caption> Informe os docentes para lecionar na turma</caption>
		<c:if test="${ not empty turmaRedeMBean.docentes}">
			<thead>
				<tr>
					<th><input type="checkbox" onclick="checkAll()" title="Selecionar Todos"/></th>
					<th width="35%">Docentes</th>
					<th>Tipo</th>				
				</tr>
			</thead>		
			<tbody>
					<c:forEach items="#{turmaRedeMBean.docentes}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td width="1%"> 
								<input type="checkbox" name="selecionados" id="check_${d.id}" value="${d.id}" class="check"/>
							</td> 
							<td width="70%">${d.nome}</td>
							<td>${d.tipo.descricao}</td>
						</tr>			
					</c:forEach>
			</tbody>
		</c:if>
		<c:if test="${ empty turmaRedeMBean.docentes}">
			<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="3" align="center" width="1%"> 
				<div style="color:red;font-weight:bold;">Não existem docentes associados ao campus.</div>
				</td>
			</tr>	
		</c:if>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Adicionar Docentes" action="#{turmaRedeMBean.adicionarDocente}" id="addDocente" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	
	<div class="infoAltRem" style="width:90%">
		<img src="${ctx}/img/delete.gif"/>: Remover Docente
	</div>
	
	<table class="formulario" style="width: 90%">
		<caption>Docentes Escolhidos</caption>
		<c:if test="${ not empty turmaRedeMBean.docentesEscolhidos}">
		<thead>
			<tr>
				<th width="35%">Docentes</th>
				<th>Tipo</th>		
				<th width="1%"></th>		
			</tr>
		</thead>		
		<tbody>
				<c:forEach items="#{turmaRedeMBean.docentesEscolhidos}" var="d" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>${d.nome}</td>
						<td>${d.tipo.descricao}</td>
						<td>
							<h:commandLink title="Fechar" style="border: 0;" action="#{turmaRedeMBean.removerDocente}" onclick="#{ confirmDelete }">
								<f:param name="idDocente" value="#{d.id}" />
								<h:graphicImage url="/img/delete.gif" alt="Remover Docente" />
							</h:commandLink>
						</td>
					</tr>			
				</c:forEach>
		</tbody>
		</c:if>	
		<c:if test="${ empty turmaRedeMBean.docentesEscolhidos}">
			<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="3" align="center" width="1%"> 
					<div style="color:red;font-weight:bold;">Nenhum docente adicionado na turma.</div>
				</td> 
			</tr>	
		</c:if>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="<< Selecionar Campus" action="#{turmaRedeMBean.telaSelecaoCampus}" id="voltarTelaCampus" rendered="#{!turmaRedeMBean.alterar}"/>
					<h:commandButton value="<< Selecionar Componente" action="#{turmaRedeMBean.telaSelecaoComponentes}" rendered="#{!turmaRedeMBean.alterar}" id="voltarTelaComponente2"/>
					<h:commandButton value="<< Selecionar Turma" action="#{turmaRedeMBean.telaBuscarTurmas}" id="turmas2" rendered="#{turmaRedeMBean.alterar}"/>
					<h:commandButton value="<< Selecionar Dados Gerais" action="#{turmaRedeMBean.telaDadosGerais}" id="dadosGerais2"/>
					<h:commandButton value="<< Selecionar Discentes" action="#{turmaRedeMBean.telaSelecaoDiscentes}" id="voltarTelaDiscentes2"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{turmaRedeMBean.cancelar}" id="cancelar2" />
					<h:commandButton value="Próximo Passo >>" action="#{turmaRedeMBean.submeterDocentes}" id="selecionar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<script type="text/javascript">
function checkAll() {
	marcar = !marcar;
	$A(document.getElementsByClassName('check')).each(function(e) {
		if (!marcar)			
			e.checked = false;
		else
			e.checked = true;
	});
}
var marcar = false;
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>