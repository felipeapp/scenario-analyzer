<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/javascript/cronograma.js"> </script>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Cadastro do Cronograma do Projeto </h2>


	<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td>Nesta tela deve ser informado cronograma do	Projeto.</td>
			<td><%@include file="passos_projeto.jsp"%>
			</td>
		</tr>
	</table>
	</div>


	<div class="infoAltRem">
		 <a href="#" onclick="javascript:cronograma.adicionarAtividade();"> <html:img src="/sigaa/img/cronograma/adicionar.gif" /> Adicionar Atividade </a>
		 <a href="#" onclick="javascript:cronograma.limpar();"> <html:img src="/sigaa/img/cronograma/limpar.gif"/> Limpar Cronograma </a>	    		    
		 <h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>: Remover Atividade
	</div>



<h:form id="frmCronograma">

	<table id="cronograma" class="formulario" width="100%">
	<caption> Cronograma de Atividades </caption>
	<thead>
		<tr>
			<th width="30%" rowspan="2"> Atividades desenvolvidas </th>
			<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano">
			<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno">
				${ano.key}
			</th>
			</c:forEach>
			<th width="2%" rowspan="2"> </th>
		</tr>
		<tr>
			<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano">
				<c:forEach items="${ano.value}" var="mes" varStatus="status">
				    <c:set var="classeCabecalho" value=""/>
				    <c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
				    <c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>    
	       			<th class="${classeCabecalho}" style="text-align: center;"> ${mes}	</th>
				</c:forEach>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:set var="numeroAtividades" value="${fn:length(projetoBase.telaCronograma.cronogramas)}" />
		<c:if test="${numeroAtividades == 0}">
			<c:set var="numeroAtividades" value="1"/>
		</c:if>

		<c:set var="valoresCheckboxes" value=",${fn:join(projetoBase.telaCronograma.calendario, ',')}" />
		<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
		<tr>
			<td class="required">
				<textarea style="width:95%; height: 30px;" name="telaCronograma.atividade" tabindex="${statusAtividades.index}">${projetoBase.telaCronograma.atividade[statusAtividades.index-1]}</textarea>
			</td>
			<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
				<c:forEach items="${ano.value}" var="mes">
					<c:set var="valorCheckbox" value="${statusAtividades.index-1}_${mes}_${ano.key}" />
					<td align="center">
						<input type="checkbox"
							class="noborder"
							id="${statusAtividades.index}_${mes}_${ano.key}"							
							name="telaCronograma.calendario"
							value="${valorCheckbox}"
							<c:if test="${fn:contains(valoresCheckboxes, valorCheckbox)}"> checked="checked" </c:if>
						/>
					</td>
				</c:forEach>
			</c:forEach>
			<td align="center">
				<a href="#" onclick="javascript:cronograma.removerAtividade(this);">
					<img src="/sigaa/img/cronograma/remover.gif" alt="Remover Atividade" title="Remover Atividade"/>
				</a>
			</td>
		</tr>
		</c:forEach>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="${projetoBase.telaCronograma.numeroMeses + 2}">
				<h:commandButton value="#{projetoBase.confirmButton}" action="#{projetoBase.atualizarCronograma}"  id="btAtualizarCronograma" rendered="#{projetoBase.confirmButton == 'Salvar'}"/>
				<h:commandButton value="<< Voltar" action="#{projetoBase.passoAnterior}"  id="btPassoAnteriorCronograma" rendered="#{projetoBase.confirmButton != 'Salvar'}"/>
				<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}" onclick="#{confirm }" id="btCancelar"/>
				<h:commandButton value="Gravar e Avançar >>" action="#{projetoBase.submeterCronograma}"  id="btSubmeterCronograma" rendered="#{projetoBase.confirmButton != 'Salvar'}"/> 
			</td>
		</tr>
	</tfoot>	
	</table>
	<br/><center><h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
	
</h:form>

</f:view>
<script type="text/javascript">
	var cronograma = new Cronograma('frmCronograma',
		'${projetoBase.telaCronograma.mesesString}',
		'${numeroAtividades}'
	);
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>