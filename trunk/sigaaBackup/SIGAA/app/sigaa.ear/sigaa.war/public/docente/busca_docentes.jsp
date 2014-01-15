<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2> Docentes da ${ configSistema['siglaInstituicao'] } </h2>

<style>
	td.foto {
		padding: 2px 5px;
	}

	span.nome {
		display: block;
		font-weight: bold;
		line-height: 1.25em;
		font-size: 1.1em;
	}
	span.departamento {
		display: block;
	}
	span.pagina {
		display: block;
		margin-top: 10px;
	}

	span.pagina a {
		padding-left: 20px;
		background: url(${ctx}/public/images/docente/icones/perfil.png) no-repeat;
		color: #D59030;
	}
</style>

<f:view>
	<h:outputText value="#{portalPublicoDocente.create}"/>

	<div class="descricaoOperacao">
		Nesta página você pode buscar os docentes atuantes na ${ configSistema['siglaInstituicao'] }. Busque 
		pelo seu nome ou departamento de lotação.
	
	</div>

	<h:form id="form">
		<h:messages showDetail="true"></h:messages>
		<table class="formulario" align="center" width="75%">
		<caption class="listagem">Informe os critérios de consulta</caption>

			<tr>
				<th width="20%">Nome:</th>
				<td>
					<h:inputText id="nome" value="#{portalPublicoDocente.docente.pessoa.nome}" style="width:75%"/>
				</td>
			</tr>
			<tr>
				<th>Departamento:</th>
				<td>
					<h:selectOneMenu id="departamento" value="#{portalPublicoDocente.docente.unidade.id}" style="width:95%">
						<f:selectItem itemLabel=" -- TODOS -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allDeptosEscolasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar"  action="#{portalPublicoDocente.buscarDocentes}" value="Buscar"/>&nbsp;
					<h:commandButton id="cancelar" immediate="true"  action="#{portalPublicoDocente.cancelar}" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>

	<c:if test="${not empty docentes}">
		<br>
		<table class="listagem" style="width: 75%;">
			<caption> Docentes encontrados (${fn:length(docentes)}) </caption>
			<tbody>
				<c:forEach var="docente" items="${docentes}" varStatus="status">

				<c:set var="stripes">${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }</c:set>
				<tr class="${stripes} topo">
					<td class="foto">
						<c:if test="${docente.idFoto != null}">
							<img src="${ctx}/verFoto?idFoto=${docente.idFoto}&key=${ sf:generateArquivoKey(docente.idFoto) }" height="100"/>
						</c:if>
						<c:if test="${docente.idFoto == null}">
							<img src="${ctx}/img/no_picture.png" height="100"/>
						</c:if>
					</td>
					<td><br>
						<span class="nome">${docente.nome}</span>
						<span class="departamento"> ${docente.unidade.nome} </span>
						<span class="pagina">
							<a href="${ctx}/public/docente/portal.jsf?siape=${docente.siape}" title="Clique aqui para acessar a página pública deste docente">
								ver página pública
							</a>
						</span>
					</td>
				</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center"> <b>${fn:length(docentes)} docentes encontrados </b></td>
				</tr>
			</tfoot>
		</table>
	</c:if>
</f:view>

<br />
<%@include file="/public/include/rodape.jsp" %>