<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<style>
	.negativo{ text-align: right; color: red; }
	.positivo{ text-align: right; }
	.label{font-weight: bold;}
</style>
<f:view>
<a4j:keepAlive beanName="curriculoMedio"/>
<h2> <ufrn:subSistema /> &gt; Visualizar Estrutura Curricular</h2>

<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Dados da Estrutura Curricular de Ensino Médio</caption>
			<tr>
				<th class="label" width="40%">Código do Currículo:</th>
				<td><h:outputText value="#{curriculoMedio.obj.codigo}"/></td>
			</tr>
			<tr>
				<th class="label">Curso:</th>
				<td colspan="5">
					<h:outputText value="#{curriculoMedio.obj.cursoMedio.nomeCompleto}"/> 
				</td>
			</tr>
			<tr>
				<th class="label">Série:</th>
				<td>
					<h:outputText value="#{ curriculoMedio.obj.serie.descricao}"/>
				</td>
			</tr>
			<tr>
				<th class="label">Carga Horária Total:</th>
				<td>
					<h:outputText value="#{curriculoMedio.obj.cargaHoraria} h"/>
				</td>
			</tr>
			<tr>
				<th class="label">Ano de Entrada em Vigor:</th>
				<td>
					<h:outputText value="#{curriculoMedio.obj.anoEntradaVigor}"/> 
				</td>
			</tr>
			<tr>
				<th class="label">Prazo de Conclusão:</th>
				<td>
					<h:outputText value="#{curriculoMedio.obj.unidadeTempo.descricao}"/>
				</td>
			</tr>
			<tr>
				<th class="label"> Ativo: </th>
				<td>
					<h:outputText value="#{(curriculoMedio.obj.ativo?'Sim':'Não')}"/> 
				</td>
			</tr>
	</table>
	<br/><br/>
	
	<table id="tableDisciplinas" class="listagem" style="width: 70%">
		<caption>Disciplinas vinculadas na Estrutura Curricular</caption>
		<thead>
			<tr>
				<th>Disciplinas Adicionadas</th>
				<th style="text-align: right;">CH Anual</th>
			</tr>
		</thead>
		<c:set var="chTotal" value="0" />
		<tbody>	
			<c:forEach var="linha" items="#{curriculoMedio.curriculoDisciplinas}" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${linha.componente.codigo} - ${linha.componente.nome}</td>
					<td style="text-align: right;">
						<h:outputText value="#{linha.chAno}"/> h
					</td>
				</tr>		
			</c:forEach>
				<tr>
					<td style="text-align: right;"><b>Carga Horária Total:</b></td>
					<td style="text-align: right;"><h:outputText id="chTotal" value="#{curriculoMedio.obj.cargaHoraria}"/> h</td>
					<td></td>
				</tr>
				<tr>
					<td style="text-align: right;"><b>Carga Horária Preenchida:</b></td>
					<td style="text-align: right;"><h:outputText id="chPreenchida" value="#{curriculoMedio.chPreenchida}"/> h </td>
					<td></td>
				</tr>
				<tr>
					<td style="text-align: right;"><b>Carga Horária Restante:</b></td>
					<td style="text-align: right;"><h:outputText styleClass="#{ curriculoMedio.chRestante < 0 ? 'negativo':'positivo' }" id="chRestante" value="#{curriculoMedio.chRestante}"/> h </td>
					<td></td>
				</tr>
		</tbody>		
	</table>
	<br />
	<div align="center">
		<h:commandLink value="<< Voltar" action="#{curriculoMedio.listar}" id="voltar"/>
	</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>