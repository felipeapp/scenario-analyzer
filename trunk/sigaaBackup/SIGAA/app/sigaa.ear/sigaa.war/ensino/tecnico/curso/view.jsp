<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> &gt; Detalhes</h2>
<style>
.label {
	font-weight: bold;
}
</style>

<f:view>
<table class="formulario" style="width: 90%">
	  <caption>Dados do Curso</caption>
	  <tbody>
			<tr>
				<th class="label">Código no INEP:</th>
				<td><h:outputText value="#{cursoTecnicoMBean.obj.codigoInep}"/></td>
			</tr>
			<tr>
				<th class="label">Código na ${ configSistema['siglaInstituicao'] }:</th>
				<td><h:outputText value="#{cursoTecnicoMBean.obj.codigo}"/></td>
			</tr>
			<tr>
				<th class="label">Nome:</th>
				<td><h:outputText value="#{cursoTecnicoMBean.obj.nome}"/></td>
			</tr>
			<tr>
				<th class="label">Data de Início do Funcionamento: </th>
				<td>
					<t:outputText value="#{cursoTecnicoMBean.obj.dataInicioFuncionamento}">  
						 <f:converter converterId="convertData"/>
					</t:outputText>
				</td>
			</tr>
			 <tr>
				<th class="label">Carga Horária Mínima:</th>
				<td><h:outputText value="#{cursoTecnicoMBean.obj.chMinima}h "/> (em horas)</td>
			</tr>
			<tr>
				<th class="label">Modalidade: </th>
				<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.modalidadeCursoTecnico.descricao}"/>
				</td>
			</tr>
			<tr>
			 	<th class="label">Regime Letivo: </th>
			 	<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.tipoRegimeLetivo.descricao}"/>
			 	</td>
			</tr>
			<tr>
			 	<th class="label">Sistema Curricular: </th>
			 	<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.tipoSistemaCurricular.descricao}"/>
			 	</td>
			</tr>
			<tr>
			 	<th class="label">Situação do Curso: </th>
			 	<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.situacaoCursoHabil.descricao}"/>
			 	</td>
			</tr>
			<tr>
			 	<th class="label">Situação do Diploma:</th>
			 	<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.situacaoDiploma.descricao}"/>
						
			 	</td>
			</tr>
			<tr>
				<th class="label">Turno: </th>
			 	<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.turno.descricao}" />
			 	</td>
			</tr>
			<tr>
				<th class="label">Forma de Participação do Aluno:</th>
				<td>
					<h:outputText value="#{cursoTecnicoMBean.obj.modalidadeEducacao.descricao}"/>
				</td>
			</tr>
			<tr>
				<th class="label">Permite alunos com mais de um vínculo ativo?</th>
				<td>
					<h:outputText value="#{(cursoTecnicoMBean.obj.permiteAlunosVariosVinculos?'Sim':'Não')}"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<h:form>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{cursoTecnicoMBean.buscar}"/>
					</td>
				</tr>
			</h:form>
		</tfoot>
		<tfoot> 
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

