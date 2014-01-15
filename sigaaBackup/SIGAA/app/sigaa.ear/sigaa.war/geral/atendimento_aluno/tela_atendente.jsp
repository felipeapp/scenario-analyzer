<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctx" value="<%= request.getContextPath() %>"/>
<f:view>

	<style>
	
		table.subFormulario {
			height:100%;
			width:100%;
		}	
	
		table.subFormulario tr.titulo {
			border: 1px solid #CCC;
			border-width: 1px 0;
		}
		
		table.subFormulario td {
			text-align: left; 
		}
		table.subFormulario th {
			font-weight: bold; 
		}			
	</style>

	${atendimentoAluno.create}
	${atendimentoAluno.carregarPergunta}
	
	<h:form id="atendenteResponderPanel">
		<table class="subFormulario" height="100%">
			<tr>
				<td colspan="2">
					<div class="infoAltRem">
						<img src="${ctx}/img/view.gif"/> : Ver Histórico
						&nbsp;
						<img src="${ctx}/img/seta.gif"/> : Responder
					</div>
					<center>				
						<h:commandLink action="#{ buscaAvancadaDiscenteMBean.detalhesDiscente }" id="historico">
							<h:graphicImage value="/img/view.gif" alt="Ver detalhes" title="Ver detalhes"/>
							<f:param name="id" value="#{ atendimentoAluno.pergunta.discente.id }"/>
						</h:commandLink>
						<h:commandLink action="#{atendimentoAluno.preResponder}" title="Responder" id="responder">
							<f:param name="idPergunta" value="#{atendimentoAluno.pergunta.id }" />
							<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>						
					</center>
				</td>
			</tr>		
			<tr>
				<td class="subFormulario" colspan="2">Informação sobre o aluno</td>					
			</tr>							
			<tr>
				<th>Nome:</th>
				<td align="left">
					${ atendimentoAluno.pergunta.discente.nome }
				</td>
			</tr>
			<c:if test="${atendimentoAluno.pergunta.discente.graduacao}">
				<tr>
					<th> Ano/Período de Ingresso: </th>
					<td> ${atendimentoAluno.pergunta.discente.anoPeriodoIngresso} </td>
				</tr>
				<tr>
					<th> Forma de Ingresso: </th>
					<td> ${atendimentoAluno.pergunta.discente.formaIngresso.descricao} </td>
				</tr>
				<tr>
					<th width="25%"> Matriz Curricular: </th>
					<td> ${atendimentoAluno.pergunta.discente.matrizCurricular} </td>
				</tr>
				<tr>
					<th> IRA: </th>
					<td> ${atendimentoAluno.pergunta.discente.ira} </td>
				</tr>
			</c:if>
			<c:if test="${atendimentoAluno.pergunta.discente.stricto}">
				<tr>
					<th> Ano/Mês de Ingresso: </th>
					<td> ${atendimentoAluno.pergunta.discente.anoMesIngresso} </td>
				</tr>
				<tr>
					<th width="25%"> Orientador: </th>
					<td> ${atendimentoAluno.pergunta.discente.orientacao.nomeOrientador} </td>
				</tr>
				<tr>
					<th> Área de Concentração: </th>
					<td> ${atendimentoAluno.pergunta.discente.area} </td>
				</tr>
				<tr>
					<th> CR: </th>
					<td> ${atendimentoAluno.pergunta.discente.mediaGeral} </td>
				</tr>
			</c:if>
			<tr>
				<td class="subFormulario" colspan="2">Pergunta</td>					
			</tr>					
			<tr>
				<td colspan="2"><center><i>${ atendimentoAluno.pergunta.titulo }</i></center></td>
			</tr>	
			<tr class="titulo">
				<td colspan="2">${ atendimentoAluno.pergunta.pergunta }</td>
			</tr>
		</table>
		
		<br/>
		
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>