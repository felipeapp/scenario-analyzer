<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoMedio"/>
<h2> <ufrn:subSistema /> &gt; Visualizar Curso </h2>

<style>

.label { 
		font-weight:bold;
		width: 40%;
	}
</style>
	

	<table class="formulario" style="width: 90%">
	  <caption>Dados do Curso</caption>
		<tbody>
			<tr>
				<th class="label">Código no INEP:</th>
				<td><h:outputText value="#{cursoMedio.obj.codigoINEP}"/></td>
			</tr>
			<tr>
				<th class="label">Código na ${ configSistema['siglaInstituicao'] }:</th>
				<td><h:outputText value="#{cursoMedio.obj.codigo}"/></td>
			</tr>
			<tr>
				<th class="label">Nome:</th>
				<td><h:outputText value="#{cursoMedio.obj.nome}"/></td>
			</tr>
			<tr>
				<th class="label">Data de Início do Funcionamento: </th>
				<td>
					<h:outputText value="#{cursoMedio.obj.dataInicio}">  
						<f:converter converterId="convertData"/>
					</h:outputText>
				</td>
			</tr>
			<tr>
				<th class="label">Modalidade: </th>
				<td>
					<h:outputText value="#{cursoMedio.obj.modalidadeCursoMedio.descricao}" />
				</td>
			</tr>
			<tr>
			 	<th class="label">Regime Letivo: </th>
			 	<td>
					<h:outputText value="#{cursoMedio.obj.tipoRegimeLetivo.descricao}"/>
			 	</td>
			</tr>
			<tr>
			 	<th class="label">Sistema Curricular: </th>
			 	<td>
					<h:outputText value="#{cursoMedio.obj.tipoSistemaCurricular.descricao}"/>
			 	</td>
			</tr>

			<tr>
			 	<th class="label">Situação do Curso: </th>
			 	<td>
					<h:outputText value="#{cursoMedio.obj.situacaoCursoHabil.descricao}"/>
			 	</td>
			</tr>
			<tr>
			 	<th class="label">Situação do Diploma:</th>
			 	<td>
					<h:outputText value="#{cursoMedio.obj.situacaoDiploma.descricao}"/>
			 	</td>
			</tr>
			<tr>
				<th class="label">Turno: </th>
			 	<td>
					<h:outputText value="#{cursoMedio.obj.turno.descricao}"/>
			 	</td>
			</tr>
			<tr>
				<th class="label">Forma de Participação do Aluno:</th>
				<td>
					<h:outputText value="#{cursoMedio.obj.modalidadeEducacao.descricao}"/>
				</td>
			</tr>
			<tr>
				<th class="label"> Ativo: </th>
				<td>
					<h:outputText value="#{(cursoMedio.obj.ativo?'Sim':'Não')}"/> 
				</td>
			<tr>
		</tbody>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:form>
						<h:commandButton value="<< Voltar" action="#{cursoMedio.listar}"/>
					</h:form>
				</td>
		   </tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>