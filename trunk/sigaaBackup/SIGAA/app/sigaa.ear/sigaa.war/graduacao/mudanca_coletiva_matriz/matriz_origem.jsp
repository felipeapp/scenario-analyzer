<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<f:view>

<style>
	tr.componente td{
		background: #C4D2EB;
		font-weight: bold;
		border-bottom: 1px solid #BBB;
		color: #222;
	}
</style>

	<h2> <ufrn:subSistema /> &gt; Mudança Coletiva de Matriz Curricular  &gt; Definir Matriz Curricular de Origem</h2>
	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		<p>Esta operação efetua a mudança coletiva de matrizes curriculares, para um grupo de alunos selecionados, conforme a necessidade do DAE.</p>
		
		<p>- Ao selecionar as informações da matriz curricular de origem, serão listados os alunos vinculados a matriz, 
		que possibilitará o Administrador do DAE selecionar os discentes que sofrerão a mudança de matriz curricular.</p>
		
		<p>- Em seguida o sistema listará todos aqueles que foram selecionados, 
		no qual o usuário informará a matriz curricular de destino, encerrando a operação.</p>
	</div>
	<h:form id="formulario">
	<table class="formulario" width="95%">
		<caption class="formulario">Buscar Discentes por Matriz de Origem</caption>
		<tbody>
			<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu value="#{mudancaColetivaMatrizCurricular.matrizAtual.curso.id}" id="curso" 
							valueChangeListener="#{mudancaColetivaMatrizCurricular.carregarMatrizes}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
						<a4j:support event="onchange" reRender="matrizCurricular, curriculo"
							onsubmit="$('formulario:indicatorMatriz').style.display='inline'; 
									$('formulario:indicatorCurriculo').style.display='inline';
									$('formulario:matrizCurricular').style.display='none';
									$('formulario:curriculo').style.display='none';" 
							oncomplete="$('formulario:indicatorMatriz').style.display='none'; 
									$('formulario:indicatorCurriculo').style.display='none';
									$('formulario:matrizCurricular').style.display='inline';
									$('formulario:curriculo').style.display='inline';"/>
					</h:selectOneMenu>
					<h:graphicImage value="/img/indicator.gif" id="indicatorCurso" style="margin-left:5px; display: none;" />
				</td>
			</tr>
			<tr>
				<th class="required">Matriz Curricular:</th>
				<td>
					<h:graphicImage value="/img/indicator.gif" id="indicatorMatriz" style="display: none;" />
					<h:selectOneMenu value="#{mudancaColetivaMatrizCurricular.matrizAtual.id}" id="matrizCurricular" 
							valueChangeListener="#{ mudancaColetivaMatrizCurricular.carregarCurriculosOrigem }" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{mudancaColetivaMatrizCurricular.matrizes}" />
						<a4j:support event="onchange" reRender="curriculo" 
							onsubmit="$('formulario:indicatorCurriculo').style.display='inline';
									$('formulario:curriculo').style.display='none';"
							oncomplete="$('formulario:indicatorCurriculo').style.display='none';
									$('formulario:curriculo').style.display='inline';"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th>Estrutura Curricular:</th>
				<td>
					<h:graphicImage value="/img/indicator.gif" id="indicatorCurriculo" style="display: none;" />
					<h:selectOneMenu value="#{mudancaColetivaMatrizCurricular.curriculoAtual.id}" id="curriculo" >
						<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
						<f:selectItems value="#{mudancaColetivaMatrizCurricular.curriculosOrigem}" />
					</h:selectOneMenu> 
				</td>
			</tr>
			
			<tr>
				<th>Ano de Ingresso:</th>
				<td>
					<h:inputText value="#{mudancaColetivaMatrizCurricular.anoIngresso}" id="anoIngresso" maxlength="4" size="4"/> 
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar Discentes" action="#{mudancaColetivaMatrizCurricular.buscarDiscentes}" id="btnBuscar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{mudancaColetivaMatrizCurricular.cancelar}" id="btnCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>