<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>
<f:view>
	<h2>
		<ufrn:subSistema/> > Estrutura Curricular de Matrizes Curriculares &gt; Aproveitar Componentes Curricular
	</h2>
	
	<h:form id="buscaCC">
		<table class="formulario" width="90%">
			<caption>Componentes Curriculares por um Currículo Base</caption>
			<tbody>
			<c:if test="${acesso.administradorDAE}">
				<tr>
					<th class="required">Curso:</th>
					<td>
						<h:selectOneMenu id="selectCurso" style="width: 98%;" onchange="submit()" 
								valueChangeListener="#{curriculo.carregarCurriculos }">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{curriculo.cursosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${not acesso.administradorDAE}">
				<tr>
					<th>Curso:</th>
					<td>
						 ${ curriculo.obj.curso.descricao }
					</td>
				</tr>			
				<tr>
					<th>Matriz:</th>
					<td>
						${curriculo.obj.matriz.descricaoMin }
					</td>
				</tr>			
			</c:if>
						
			<tr>
				<th class="required">Currículos:</th>
				<td>
					<h:selectOneMenu id="curriculo" onchange="submit()" valueChangeListener="#{curriculo.carregarComponentesDeCurriculoBase }" >
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{curriculo.curriculosBase}" />
					</h:selectOneMenu>
				</td>
			</tr>
	<c:if test="${not empty curriculo.curriculoComponentes}">
		<tr>
			<td colspan="2">
				<table class="listagem" width="100%">
					<caption>${fn:length(curriculo.curriculoComponentes) } Componentes Econtrados </caption>
					<tbody>
					<c:set var="semestreAtual" value="0" />
					<c:forEach items="${curriculo.curriculoComponentes }" var="cc" varStatus="status" >
						<c:if test="${ semestreAtual != cc.semestreOferta}">
							<c:set var="semestreAtual" value="${cc.semestreOferta}" />
							<tr class="destaque"><td colspan="3" style="font-variant: small-caps;">
								${semestreAtual} º Período
							</td></tr>
						</c:if>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td style="font-size: xx-small; font-style: italic;" width="12%">
							<input type="checkbox" name="obrigatorias" value="${status.index}" onfocus="$('opt_'+this.value).checked=false" id="obr_${status.index}"
							${(cc.obrigatoria)?"checked":""} >
							<label for="obr_${status.index}">Obrigatória</label>
							</td>
							<td style="font-size: xx-small; font-style: italic;" width="12%">
							<input type="checkbox" name="optativas" value="${status.index}" onfocus="$('obr_'+this.value).checked=false" id="opt_${status.index}"
							${(!cc.obrigatoria)?"checked":""}>
							<label for="opt_${status.index}">Optativa</label>
							</td>
							<td>
							${cc.componente.descricao}
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
	</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
							<c:if test="${not empty curriculo.curriculoComponentes}">
							<h:commandButton value="Selecionar esses Componentes Curriculares" style="width: 400px" action="#{curriculo.aproveitarComponentes}" id="submeter" /><br><br>
							</c:if>
							<h:commandButton value="<< Dados Gerais" action="#{curriculo.voltarDadosGerais}"  id="voltar"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}"  action="#{curriculo.cancelar}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>

	<br>
	<div style="text-align: center;">
	</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
