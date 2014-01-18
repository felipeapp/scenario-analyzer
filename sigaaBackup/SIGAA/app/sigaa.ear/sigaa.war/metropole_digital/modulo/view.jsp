<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.titulo { font-weight: bold; background-color: #ccc;}
	.desc  { font-weight: bold }
</style>

<f:view>
<a4j:keepAlive beanName="qualificacaoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Visualização de Módulo</h2>
<h:form id="form">
	<table class="formulario" style="width: 90%">
		<caption>Dados do Módulo</caption>

		<tr>
			<td class="desc linhaImpar">Código</td>
			<td class="desc linhaImpar">${moduloIMDMBean.obj.codigo}</td>
		</tr>		

		<tr>
			<td class="desc linhaPar">Descrição</td>
			<td class="desc linhaPar">${moduloIMDMBean.obj.descricao}</td>
		</tr>		
		
		<tr>
			<td class="desc linhaImpar">CH TOTAL MODULO</td>
			<td class="desc linhaImpar">${moduloIMDMBean.obj.cargaHoraria} ch <span style="font-style: italic; font-weight: normal;">(Esse valor é manual e definido na criação do módulo)</span></td>
		</tr>		
		
		<tr>
			<td class="subFormulario" colspan="2">Componentes</td>
		</tr>
		<tr>
			<td class="titulo">Disciplina</td>
			<td class="titulo">CH</td>
		</tr>
		<c:forEach items="#{moduloIMDMBean.obj.moduloDisciplinas}" var="md" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar'}">
				<td>${md.disciplina.codigoNome }</td>
				<td>${md.disciplina.chTotal} ch</td>
			</tr>
		</c:forEach>
		<tr>
			<td class="subFormulario" colspan="2">Estruturas Curriculares relacionadas</td>
		</tr>
		<tr>
			<td class="titulo">Ano-Período</td>
			<td class="titulo">Curso</td>
		</tr>
		<c:forEach items="#{moduloIMDMBean.obj.moduloCurriculares}" var="mc" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar'}">
				<td>${mc.estruturaCurricularTecnica.anoEntradaVigor}.${mc.estruturaCurricularTecnica.periodoEntradaVigor }</td>
				<td>${mc.estruturaCurricularTecnica.cursoTecnico.nome }</td>
			</tr>
		</c:forEach>
		
		<tfoot>
			<tr>
				<td align="center" colspan="2">
					<input type="button" value="Cancelar" onclick="javascript:history.go(-1)" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>