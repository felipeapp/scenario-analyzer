<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="relatoriosDepartamentoCpdi"></a4j:keepAlive>
	<h:outputText value="#{relatoriosDepartamentoCpdi.create}" />
	<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > ${relatoriosDepartamentoCpdi.titulo} </h2>
<div id="ajuda" class="descricaoOperacao">
		<p>Caro Usuário, </p>
		<p>
		esta operação permite ao gestor gerar um relatório com os dados quantitativos dos departamentos, cursos, docentes, 
		discentes, técnicos administrativos, teses/dissertações, bases de pesquisa e projetos de extensão associados 
		para unidade acadêmica de acordo com o ano e período selecionado. 
		</p>
		<p>
			Segue abaixo mais informações de como é contabilizado os dados quantitativos em relação ao ano e período selecionado:
			<ul>
				<li><b>Departamentos:</b>Todos departamentos, não extintos, criados até o ano e período informado.</li>
				<li><b>Cursos de Pós-Graduação Stricto Sensu:</b>Todos os cursos reconhecidos até o ano e período informado;</li>
				<li>
					<b>Cursos de Pós-Graduação Lato Sensu:</b>Todos os cursos, com situação da proposta 
					aceita, não finalizados, que tenham iniciado até o ano e período informado.
				</li>
				<li><b>Cursos de Graduação:</b>Todos os cursos criados até o ano e período informado;</li>
				<li><b>Docentes:</b>Todos os docentes onde o ano e período informado dentro do intervalo da data de admissão e a data de desligamento. </li>
				<li>
					<b>Discentes:</b> Todos os discentes ingressantes até o ano e período informado. No caso  
					dos matriculados, o ano e período da matrícula no componente também é levado em consideração;
				</li>
				<li><b>Teses/Dissertação:</b>Todas as teses/dissertações com banca cadastrada até o ano e período informado. </li>
				<li><b>Bases de Pesquisa:</b>Todas as bases de pesquisa criadas até o ano e período informado.</li> 
				<li><b>Projetos de Extensão:</b>Todos os projetos em execuçao, concluídos ou aprovados, onde o ano e período informados estão dentro do intervalo da data inicial e final do projeto de extensão. </li>
			</ul>  
		</p>
</div>
<h:form id="form">
	
	<table class="formulario" style="width: 95%">
	<caption> Informe os critérios para a emissão do relatório </caption>
		<tr>
			<th><b>Centro:</b></th>
			<td>
				${usuario.vinculoAtivo.unidade.unidadeGestora.codigoNome}
			</td>
		</tr>
		
		<c:if test="${relatoriosDepartamentoCpdi.necessitaPeriodo}">
		<tr>
			<th class="required">Ano.Período:</th>
			<td>
				<h:inputText id="ano" value="#{relatoriosDepartamentoCpdi.ano}" 
					size="4" maxlength="4" onkeyup="formatarInteiro(this)" />
				.<h:inputText id="semestre" value="#{relatoriosDepartamentoCpdi.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this)" />
			</td>
		</tr>
		</c:if>
		
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{relatoriosDepartamentoCpdi.gerarRelatorioGerencial}" value="Emitir Relatório"/>
				<h:commandButton action="#{relatoriosDepartamentoCpdi.cancelar}" value="Cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
		
</h:form>

<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>