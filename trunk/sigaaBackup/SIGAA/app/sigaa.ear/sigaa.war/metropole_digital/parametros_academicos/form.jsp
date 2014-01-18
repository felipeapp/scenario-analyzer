<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="parametrosAcademicosIMD" />
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>
<f:view>

	<h2>
		<ufrn:subSistema />
		> Par�metros Acad�micos
	</h2>

	<h:form >
		<div class="operacaoDescricao">
			<p></p>
		</div>


		<table class="formulario" width="100%">
			<tbody>
			<caption>Par�metros Acad�micos</caption>

			<tr>
				<td class="subFormulario" colspan="2">AVALIA��O</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Frequ�ncia M�nima para Aprova��o:</th>
				<td>
				<h:inputText
						value="#{parametrosAcademicosIMD.obj.frequenciaMinimaAprovacao}"  size="6" maxlength="4" style="text-align:right" onkeypress="return(formatarMascara(this,event,'##.#'))"  title="Frequ�ncia m�nima para aprova��o" required="true"/>
							
					<ufrn:help img="/img/ajuda.gif">
						Frequ�ncia m�nima para aprova��o do aluno no m�dulo. Valor em Porcentagens.
					</ufrn:help>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Nota M�nima para Recupera��o:</th>
				<td>
					<h:inputText
						value="#{parametrosAcademicosIMD.obj.notaMinimaRecuperacao}" size="6" maxlength="3" style="text-align:right" onkeypress="return(formatarMascara(this,event,'#.#'))" title="Nota m�nima para recupera��o" required="true"/>
						
											
					<ufrn:help img="/img/ajuda.gif">
						Nota m�nima que o aluno dever� obter no m�dulo para que n�o reprove e participe da recupera��o.
					</ufrn:help>	
						
				</td>
									
			</tr>

			<tr>
				<th class="obrigatorio">Nota M�nima por Disciplina:</th>
				<td>
					<h:inputText
						value="#{parametrosAcademicosIMD.obj.notaReprovacaoComponente}" size="6" maxlength="3" style="text-align:right" onkeypress="return(formatarMascara(this,event,'#.#'))" title="Nota m�nima por Disciplina" required="true"/>							
					<ufrn:help img="/img/ajuda.gif">
						Nota m�nima que o aluno dever� obter em cada disciplina para que n�o reprove e participe da recupera��o.  
					</ufrn:help>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">M�dia M�nima para Aprova��o:</th>
				<td>
					<h:inputText
						value="#{parametrosAcademicosIMD.obj.mediaAprovacao}" size="6" maxlength="3" style="text-align:right" onkeypress="return(formatarMascara(this,event,'#.#'))" title="M�dia m�nima para aprova��o" required="true"/>						
					<ufrn:help img="/img/ajuda.gif">
						M�dia m�nima necess�ria para aprova��o do aluno no m�dulo.
					</ufrn:help>						
				</td>						
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar Par�metros"
							action="#{parametrosAcademicosIMD.cadastrar}"/> 
						<h:commandButton
							value="Cancelar" action="#{parametrosAcademicosIMD.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

	</h:form>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>