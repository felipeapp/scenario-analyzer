<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> &gt; ${processamentoMatriculaDiscenteSerie.labelCombo} de Consolidação de Discentes em Série</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<p>Utilize esta operação para ${processamentoMatriculaDiscenteSerie.processamento ? 'realizar a consolidação' : 'emitir o resultado'} da situação dos discentes de determinado curso por série.</p>
		<br/>
		<ul>
			<li>Informe o ano e curso para ${processamentoMatriculaDiscenteSerie.processamento ? 'solicitar o processamento' : 'emitir o resultado'} da situação dos discentes na série com base nos aproveitamentos 
			das disciplinas dos mesmos.</li>
			<li>O aluno com uma ou mais reprovações em disciplinas do ano será considerado como <b>REPROVADO</b> na série. 
			Possibilitando o coordenador caracterizá-lo como <b>APROVADO EM DEPENDÊNCIA</b> na operação destinada a tal.</li>
		</ul>	 
	</div>

	<table class="formulario" width="60%">
	<caption>Dados do Processamento para Consolidação de Série</caption>
	<tbody>
		<tr>
			<th class="obrigatorio">Ano: </th>
			<td><h:inputText value="#{ processamentoMatriculaDiscenteSerie.ano }" onkeyup="return formatarInteiro(this);" maxlength="4" size="4"/></td>
		</tr>
	
		<tr>
			<th>Curso: </th>
			<td>
				<h:selectOneMenu value="#{ processamentoMatriculaDiscenteSerie.curso.id }">
					<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					<f:selectItems value="#{ cursoMedio.allCombo }"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<c:if test="${processamentoMatriculaDiscenteSerie.processamento}">
				<c:set var="exibirApenasSenha" value="true" scope="request"/>
				<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
			</c:if>
			</td>
		</tr>
		
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{ processamentoMatriculaDiscenteSerie.confirmButton }" id="confirmar" action="#{ processamentoMatriculaDiscenteSerie.processar }"/>
				<h:commandButton value="Cancelar" action="#{ processamentoMatriculaDiscenteSerie.cancelar }" immediate="true"/>
			</td>
		</tr>
	</tfoot>
	</table>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>