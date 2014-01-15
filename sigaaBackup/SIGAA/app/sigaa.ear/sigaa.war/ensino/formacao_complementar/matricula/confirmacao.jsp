<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2>
		<ufrn:subSistema /> &gt; Matrícula de Discente &gt; Confirmação
	</h2>
	
	<h:form id="formConfirmacao">
	
		<table  class="subFormulario" align="center">
			<tr>
			<td width="8%" valign="middle" align="center">
				<html:img page="/img/warning.gif"/>
			</td>
			<td valign="middle" style="text-align: justify">
				Por favor imprima o comprovante clicando no ícone ao lado
				para maior segurança dessa operação.
			</td>
			<td>
			<table>
				<tr>
					<td align="center">
						<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{matriculaFormacaoComplementarMBean.exibirAtestadoMatricula}" id="printComprovante" >
				 			<h:graphicImage url="/img/printer_ok.png" />
				 		</h:commandLink>
				 	</td>
				 </tr>
				 <tr>
				 	<td style="font-size: medium;">
				 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{matriculaFormacaoComplementarMBean.exibirAtestadoMatricula}"  id="imprimirComprovante"/>
				 	</td>
				 </tr>
			</table>
			</td>
			</tr>
		</table>
			
	<br/>
	<%@ include file="_info_discente.jsp"%>
	
	<c:if test="${not empty matriculaFormacaoComplementarMBean.turmas}">
	<br>
	<table class="listagem" style="width: 100%">
		<caption>Turmas</caption>
		<thead>
			<tr>
			<td>Componente Curricular</td>
			<td width="2%">Turma</td>
			<td width="10%">Local</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaFormacaoComplementarMBean.turmas}" var="turma" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td>${turma.disciplina.descricao}</td>
				<td width="12%">Turma ${turma.codigo}</td>
				<td width="12%">${turma.local}</td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td colspan="3">
					Docente(s): ${turma.docentesNomes }
				</td>
			</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3" align="center">
					&nbsp;
				</td>
			</tr>
		</tfoot>
	</table>
	</c:if>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>