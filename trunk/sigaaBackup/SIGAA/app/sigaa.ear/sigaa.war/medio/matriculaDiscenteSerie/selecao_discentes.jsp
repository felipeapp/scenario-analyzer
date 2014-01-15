<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
<script type="text/javascript" charset="ISO-8859">
	var J = jQuery.noConflict();
</script>
<a4j:keepAlive beanName="matriculaDiscenteSerieMBean"/>	
<f:view>
	<h2><ufrn:subSistema /> &gt; Sinalizar Alunos para Dependência</h2>
	
	<h:form id="form" prependId="false">
	
	<table class="visualizacao" style="width: 60%">
		<tr>
			<th>Curso:</th>
			<td>${matriculaDiscenteSerieMBean.serie.cursoMedio.nomeCompleto}</td>
		</tr>
		<tr>
			<th>Série:</th>
			<td>${matriculaDiscenteSerieMBean.serie.descricaoCompleta}</td>
		</tr>
		<tr>
			<th>Ano:</th>
			<td>${matriculaDiscenteSerieMBean.ano}</td>
		</tr>
	</table>
	<br/>
	
	<table class="formulario" style="width: 90%">
		<caption> Selecione os alunos para sinalizá-los como Aprovados em Dependência na série</caption>
		<tbody>
			
			<c:forEach items="#{matriculaDiscenteSerieMBean.serie.turmas}" var="turma" varStatus="status">
				<tr>
					<td colspan="5">
					<table class="listagem" style="width: 100%">
						<caption>Turma: ${turma.nome}</caption>
						<thead>
							<tr>
								<th width="1px;"><h:selectBooleanCheckbox id="btnSelecionarTodos_${turma.id}" style="margin-left:6px;" onclick="checkAll(#{turma.id})"/></th>
								<th><label for="btnSelecionarTodos_${turma.id}">Todos</label></th>
								<th width="60%">Discente</th>
								<th width="15%" style="text-align: center">Dependência</th>
								<th width="15%">Situação Atual</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="#{turma.alunos}" var="aluno" varStatus="status">
								<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td colspan="2"> 
										<c:set var="id" value="${status.index}"/>
										<h:selectBooleanCheckbox value="#{aluno.selected}" id="mat_${turma.id}_${status.index}" styleClass="check_#{turma.id}"/> 					
									</td> 
									<td><label for="mat_${turma.id}_${status.index}">${aluno.discenteMedio.matriculaNome}</label> </td>
									<td align="center"><ufrn:format type="simnao" valor="${aluno.dependencia}"/> </td>
									<td>${aluno.situacaoMatriculaSerie.descricao}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					</td>
				</tr>	
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan=5">
					<h:commandButton value="Selecionar Discentes" action="#{matriculaDiscenteSerieMBean.selecionarDiscentes}" id="selecionar"/>
					<h:commandButton value="<< Selecionar Outra Turma" action="#{matriculaDiscenteSerieMBean.iniciarPreReprovados}" id="outra"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matriculaDiscenteSerieMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<script type="text/javascript">

	function checkAll(id) {
		J('.check_'+id).each(function(e) {
			var checkAll = J("#btnSelecionarTodos_"+id).attr("checked");
			if (!checkAll)
				checkAll = false;
			J(this).attr("checked",checkAll);
		});
	}

	

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>