<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="parametrosAcademicosIMD" />
<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>
<f:view>

	<h2>
		<ufrn:subSistema />
		> Parâmetros Acadêmicos
	</h2>

	<h:form >
		<div class="operacaoDescricao">
			<p></p>
		</div>


		<table class="formulario" width="100%">
			<tbody>
			<caption>Parâmetros Acadêmicos</caption>

			<tr>
				<td class="subFormulario" colspan="2">AVALIAÇÃO</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Frequência Mínima para Aprovação:</th>
				<td>
				<h:inputText
						value="#{parametrosAcademicosIMD.obj.frequenciaMinimaAprovacao}"  size="6" maxlength="4" style="text-align:right" onkeypress="return(formatarMascara(this,event,'##.#'))"  title="Frequência mínima para aprovação" required="true"/>
							
					<ufrn:help img="/img/ajuda.gif">
						Frequência mínima para aprovação do aluno no módulo. Valor em Porcentagens.
					</ufrn:help>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Nota Mínima para Recuperação:</th>
				<td>
					<h:inputText
						value="#{parametrosAcademicosIMD.obj.notaMinimaRecuperacao}" size="6" maxlength="3" style="text-align:right" onkeypress="return(formatarMascara(this,event,'#.#'))" title="Nota mínima para recuperação" required="true"/>
						
											
					<ufrn:help img="/img/ajuda.gif">
						Nota mínima que o aluno deverá obter no módulo para que não reprove e participe da recuperação.
					</ufrn:help>	
						
				</td>
									
			</tr>

			<tr>
				<th class="obrigatorio">Nota Mínima por Disciplina:</th>
				<td>
					<h:inputText
						value="#{parametrosAcademicosIMD.obj.notaReprovacaoComponente}" size="6" maxlength="3" style="text-align:right" onkeypress="return(formatarMascara(this,event,'#.#'))" title="Nota mínima por Disciplina" required="true"/>							
					<ufrn:help img="/img/ajuda.gif">
						Nota mínima que o aluno deverá obter em cada disciplina para que não reprove e participe da recuperação.  
					</ufrn:help>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Média Mínima para Aprovação:</th>
				<td>
					<h:inputText
						value="#{parametrosAcademicosIMD.obj.mediaAprovacao}" size="6" maxlength="3" style="text-align:right" onkeypress="return(formatarMascara(this,event,'#.#'))" title="Média mínima para aprovação" required="true"/>						
					<ufrn:help img="/img/ajuda.gif">
						Média mínima necessária para aprovação do aluno no módulo.
					</ufrn:help>						
				</td>						
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Confirmar Parâmetros"
							action="#{parametrosAcademicosIMD.cadastrar}"/> 
						<h:commandButton
							value="Cancelar" action="#{parametrosAcademicosIMD.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

	</h:form>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>