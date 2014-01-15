<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Componentes Curriculares com expressões inválidas</h2>

	<style>
		.invalido { color: #922; font-weight: bold; }
		.valido { color: #292; font-weight: bold; }
	</style>

	<div class="descricaoOperacao">
		<p>Caro Gestor,</p>
		<p> 
			Estão listados abaixo os componentes que possuem expressões de equivalências, pré-requisitos ou co-requisitos com formato inválido
			para que possam ser identificadas e corrigidas.
		</p>
	</div>

	<br/>
	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Componente Curricular
	</div>

	<h:form>
	<table class="formulario">
		<caption class="listagem">Componentes Curriculares Encontrados (${fn:length(verificacaoExpressoesComponentesBean.componentesInvalidos)})</caption>

		<thead>
			<tr>
				<td width="38%">Componente</td>
				<td width="20%">Equivalência(s)</td>
				<td width="20%">Pré-Requisitos</td>
				<td width="20%">Co-Requisitos</td>
				<td width="2%"></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{verificacaoExpressoesComponentesBean.componentesInvalidos}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td>${item.componente.codigoNome}</td>
					<td class="${ item.equivalenciaInvalido ? 'invalido' : 'valido'}"> 
						${item.equivalencia}
					</td>
					<td class="${ item.preRequisitoInvalido ? 'invalido' : 'valido'}"> 
						${item.preRequisito}
					</td>
					<td class="${ item.coRequisitoInvalido ? 'invalido' : 'valido'}"> 
						${item.coRequisito}
					</td>
					<td>
						<h:commandLink action="#{componenteCurricular.atualizar}">
							<h:graphicImage url="/img/alterar.gif" alt="Atualizar componente" title="Alterar Componente Curricular"/>
							<f:param name="id" value="#{item.componente.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton value="Cancelar" action="#{ componenteCurricular.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>