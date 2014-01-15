<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>

<f:view>
	<h2>Fichas Dos Alunos</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano:</th>
				<td>${ fichaDiscenteMedio.obj.ano }</td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>${ fichaDiscenteMedio.obj.serie.nomeCurso }</td>
			</tr>
			<tr>
				<th>Série:</th>
				<td>${ fichaDiscenteMedio.obj.serie.numeroSerieOrdinal }</td>
			</tr>
			<tr>
				<th>Turma:</th>
				<td>${ fichaDiscenteMedio.obj.nome }</td>
			</tr>
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" id="relatorio">
		<thead>
			<tr>
				<th>Número</th>
				<th>Discente</th>
				<th style="text-align:center;">Data de Nascimento</th>
				<th>Sexo</th>
				<th>Repetente</th>
				<th>Status</th>
				<th>Idade/Meses</th>
			</tr>
		</thead>
		<c:set var="total" value="0"></c:set>
		<c:forEach  var="aluno" items="#{fichaDiscenteMedio.obj.alunos }" varStatus="status">
			<tr style="${status.index % 2 == 0 ? 'background-color:#FFF;':'background-color:#DDD;'};">
				<c:set var="total" value="${total+1}"/>
				<td><h:outputText value="#{aluno.discenteMedio.discente.matricula}"/> </td>
				<td><h:outputText value="#{aluno.discenteMedio.nome}"/> </td>
				<td style="text-align:center;"><h:outputText value="#{aluno.discenteMedio.discente.pessoa.dataNascimento}"/></td>
				<td style="text-align:center;"><h:outputText value="#{aluno.discenteMedio.discente.pessoa.sexo}"/></td>
				<td style="text-align:center;"><h:outputText value="#{(aluno.repetente?'Sim':'Não') }"></h:outputText> </td>
				<td><h:outputText value="#{aluno.discenteMedio.discente.statusString}"/></td>
				<td style="text-align:center;"><h:outputText value="#{aluno.discenteMedio.idade}"/></td>
			</tr>
		</c:forEach>
		<tfoot>
			<tr style="font-weight:bold;">
				<td colspan="6">
					Total:
				</td>
				<td style="text-align:right;">
					${total}
				</td>
			</tr>
		</tfoot>
	</table>
	<br />
<fieldset>
<legend>Estatísticas da Turma</legend>
<jsp:useBean id="dados" class="br.ufrn.sigaa.ensino.medio.jsf.EstatisticasFichaDiscenteMedio" scope="page" /> 
<br/>
<div align="center">
	<cewolf:chart id="EstatisticasFichaDiscenteMedio" type="pie"> 
		<cewolf:colorpaint color="#D3E1F1"/> 
		<cewolf:data> 
			<cewolf:producer id="dados"> 
				<cewolf:param name="alunos" value="${ fichaDiscenteMedio.obj.alunos }"/>
				<cewolf:param name="tipo_dados" value="${1}"/>
			</cewolf:producer>
		</cewolf:data> 
	</cewolf:chart> 
	<cewolf:img chartid="EstatisticasFichaDiscenteMedio" renderer="/cewolf" width="619" height="400"/> 
</div>

<div align="center">
	<cewolf:chart id="EstatisticasFichaDiscenteMedio" type="pie"> 
		<cewolf:colorpaint color="#D3E1F1"/> 
		<cewolf:data> 
			<cewolf:producer id="dados"> 
				<cewolf:param name="alunos" value="${ fichaDiscenteMedio.obj.alunos }"/>
				<cewolf:param name="tipo_dados" value="${2}"/>
			</cewolf:producer>
		</cewolf:data> 
	</cewolf:chart> 
	<cewolf:img chartid="EstatisticasFichaDiscenteMedio" renderer="/cewolf" width="619" height="400"/> 
</div>
<div align="center">
	<cewolf:chart id="EstatisticasFichaDiscenteMedio" type="pie"> 
		<cewolf:colorpaint color="#FFFFFF"/> 
		<cewolf:data> 
			<cewolf:producer id="dados"> 
				<cewolf:param name="alunos" value="${fichaDiscenteMedio.obj.alunos }"/>
				<cewolf:param name="tipo_dados" value="${3}"/>
			</cewolf:producer>
		</cewolf:data> 
	</cewolf:chart> 
	<cewolf:img chartid="EstatisticasFichaDiscenteMedio" renderer="/cewolf" width="619" height="400"/> 
</div>


</fieldset>
	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>