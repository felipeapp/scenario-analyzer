<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<style>
	span.periodo {
		color: #292;
		font-weight: bold;
	}

	descricaoOperacao p{
		line-height: 1.25em;
		margin: 8px 10px;
	}
</style>

	<%@include file="/portais/discente/menu_discente.jsp" %>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<div class="descricaoOperacao">
		<h4>Caro(a) Aluno(a),</h4> <br />
		<p>
			O período de matrícula on-line extende-se de
			<span class="periodo">
				<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
		 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/></b>
		 	</span>.
		 	 Durante este período você poderá efetuar a matrícula nas disciplinas desejadas, de acordo com a oferta de turmas para o seu polo e grupo. Após esse período, alunos que não efetuarem a matrícula perderão a vaga. Portanto, fique atento a esse prazo.
		</p>
		<p>
			Selecione a turma desejada, confirme seus dados e clique no botão abaixo. <b>Fique atento a escolha de turma pois mudanças de turmas não serão permitidas no início do semestre.</b>
		</p>
	</div>

	<center>
	<h:form>
	
		<table class="formulario">
			<tbody>
				<tr>
					<th>Turma:</th>
					<td>
						<h:selectOneMenu value="#{matriculaGraduacao.turmaEntradaSelecionada.id }">
							<f:selectItems value="#{ matriculaGraduacao.turmasEntradaCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2" style="text-align:center;">
					<h:commandButton value="Selecionar turma >>" action="#{matriculaGraduacao.iniciarSolicitacaoMatriculaIMD}" id="iniciarSolicitTurmas"/>
				</td></tr>
			</tfoot>
		</table>
		
		<div align="center">
			<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
		</div>
	</h:form>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
