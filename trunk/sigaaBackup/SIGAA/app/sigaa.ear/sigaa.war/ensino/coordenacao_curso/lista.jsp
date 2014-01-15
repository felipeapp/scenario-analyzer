<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja cancelar esta coordenação de curso?')) return false" scope="request"/>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
</style>
<f:view>
	<h2><ufrn:subSistema /> &gt; Alteração/Substituição/Cancelamento de Coordenador</h2>
	<h:outputText value="#{coordenacaoCurso.create}" />
	<h:form id="listaCoordenadores">
		<table class="formulario" width="90%">
			<caption>Busca por Coordenador</caption>
			<tbody>
				<ufrn:subSistema teste="graduacao">
					<tr>
					<th width="10%">Curso:</th>
					<td>
						<h:selectOneMenu id="curso" value="#{coordenacaoCurso.obj.curso.id}" style="width: 95%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ cursoGrad.allCombo }"/>
						</h:selectOneMenu>
					</td>
					</tr>
				</ufrn:subSistema>
				<ufrn:subSistema teste="stricto">
					<tr>
						<th class="required" width="10%">Programa:</th>
					<td>
						<h:selectOneMenu id="programa" value="#{coordenacaoCurso.obj.unidade.id}" style="width: 95%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
						</h:selectOneMenu>
					</td>
					</tr>
				</ufrn:subSistema>
				<ufrn:subSistema teste="lato">
					<tr>
					<th width="10%">Curso:</th>
					<td>
						<h:selectOneMenu id="cursoLato" value="#{coordenacaoCurso.obj.curso.id}" style="width: 95%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ lato.allCombo }"/>
						</h:selectOneMenu>
					</td>
					</tr>
				</ufrn:subSistema>
				<ufrn:subSistema teste="tecnico">
					<tr>
					<th width="10%">Curso:</th>
					<td>
						<h:selectOneMenu id="cursoTecnico" value="#{coordenacaoCurso.obj.curso.id}" style="width: 95%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ cursoTecnicoMBean.allUnidadeCombo }"/>
						</h:selectOneMenu>
					</td>
					</tr>
				</ufrn:subSistema>
				<ufrn:subSistema teste="residencia">
					<tr>
						<th class="required" width="10%">Programa de Residência:</th>
					<td>
						<h:selectOneMenu id="programa" value="#{coordenacaoCurso.obj.unidade.id}" style="width: 95%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ unidade.allProgramaResidenciaCombo }"/>
						</h:selectOneMenu>
					</td>
					</tr>
				</ufrn:subSistema>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_buscar" value="Buscar" action="#{coordenacaoCurso.buscar}" /> 
						<h:commandButton id="btn_cancelar" value="Cancelar" onclick="#{confirm}" action="#{coordenacaoCurso.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	
	<c:if test="${empty coordenacaoCurso.resultadosBusca}">
		<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>
	<c:if test="${not empty coordenacaoCurso.resultadosBusca}">
		<br>
		<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" />:Substituir Coordenador
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:Alterar Dados do Coordenador
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Excluir Coordenação<br />
		</div>
		</center>
			<table class=listagem>
			<caption class="listagem">Lista de Coordenadores Encontrados</caption>
			<thead>
				<tr>
					<td>Nome</td>
					<ufrn:subSistema teste="graduacao"><td>Curso</td></ufrn:subSistema>
					<ufrn:subSistema teste="stricto"><td>Programa</td></ufrn:subSistema>
					<ufrn:subSistema teste="lato"><td>Curso</td></ufrn:subSistema>
					<td class="alinharCentro">Início do Mandato</td>
					<td class="alinharCentro">Fim do Mandato</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{coordenacaoCurso.resultadosBusca}" var="item" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${item.servidor.pessoa.nome}</td>
					<ufrn:subSistema teste="graduacao"><td>${item.curso.descricaoCompleta}</td></ufrn:subSistema>
					<ufrn:subSistema teste="stricto"><td>${item.unidade.nome}</td></ufrn:subSistema>
					<ufrn:subSistema teste="lato"><td>${item.curso.descricaoCompleta}</td></ufrn:subSistema>
					<td class="alinharCentro"> <ufrn:format type="data" valor="${item.dataInicioMandato}"></ufrn:format> </td>
					<td class="alinharCentro"> <ufrn:format type="data" valor="${item.dataFimMandato}"></ufrn:format> </td>
					
					<td width="5px">	
						<h:commandLink action="#{ coordenacaoCurso.substituirCoordenador }">
							<h:graphicImage value="/img/alterar_old.gif" alt="Subtituir Coordenador" title="Subtituir Coordenador"/>
							<f:param name="id" value="#{ item.id }"/>
						</h:commandLink>
					</td>
					<td width="5px">	
						<h:commandLink action="#{ coordenacaoCurso.iniciarAlterar }">
							<h:graphicImage value="/img/alterar.gif" alt="Alterar Dados do Coordenador" title="Alterar Dados do Coordenador"/>
							<f:param name="id" value="#{ item.id }"/>
						</h:commandLink>
					</td>					
					<td width="5px">			
						<h:commandLink action="#{ coordenacaoCurso.cancelarCoordenador }" onclick="#{confirmDelete}">
							<h:graphicImage value="/img/delete.gif" alt="Excluir Coordenação" title="Excluir Coordenação"/>
							<f:param name="id" value="#{ item.id }"/>
						</h:commandLink>		
					</td>
					
				</tr>
			</c:forEach>
		</table>
	</c:if>
	<br>
	
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
